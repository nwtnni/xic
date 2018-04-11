package assemble;

import ir.*;
import type.FnContext;
import interpret.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emit.ABIContext;

// TODO Currently hardcoded in AT&T syntax, add factory methods instead of dealing with pure strings
// TODO Currently hardcoded all ARG1() and _ARG1 calls

// Strings that the visits return correspond to inputs into the tile above
public class Assembler extends IRVisitor<String> {
    
    /**
     * Returns the assembly code as a String given a canonical IR AST
     *
     */
    public static String assemble(IRNode ast, ABIContext context) {
        Assembler assembler = new Assembler(context);
        ast.accept(assembler);
        return String.join("\n", assembler.cmds);
    }

    /**
     * The running list of assembly commands
     */
    public List<String> cmds;                       // Assembly Code
    private int tempCounter = 0;                    // How many temps are used
    private HashMap<String, Integer> namedTemps;    // Translates IRTemp to memory offset
    private int maxReturn = 0;                      // Amount of stack space for returns
    private int maxArgs = 0;                        // Amount of stack space for args
    private int isMultipleReturn;                   // 1 if the function returns > 2 elements
    private int returnLoc;                          // Offset from rbp that stores the mem address for multiple returns
    private String fnName;                          // TODO Name of current function used for hack

    private ABIContext context;
    /**
     * Constructor initializes @param cmds.
     */
    private Assembler(ABIContext c) {
        cmds = new ArrayList<String>();
        namedTemps = new HashMap<>();
        context = c;
    }

    /**
     * Generates temp offsets relative to rbp. Use -tempCounter(%rbp) to access.
     */
    private int genTemp() {
        tempCounter += 8;
        return tempCounter;
    }

    // TODO This may be slightly hacky
    private int numReturn(String fn) {
        if(fn.equals("_xi_alloc")) {
            return 1;
        }
        else if (fn.equals("_xi_out_of_bounds")) {
            return 0;
        }

        return context.getNumReturns(fn);

        // fn = fn.substring(fn.lastIndexOf("_")+1);
        // if(fn.charAt(0) == 'p'){
        //     return 0;
        // }
        // else if (fn.charAt(0) == 'i' || fn.charAt(0) == 'b' || fn.charAt(0) == 'a') {
        //     return 1;
        // }
        // else if (fn.charAt(0) == 't') {
        //     fn = fn.replaceAll("[^\\d]", "");    //TODO Verify that this actually works
        //     return Integer.parseInt(fn);
        // }
        // throw new RuntimeException("This is an invalid ABI name."); //TODO Fix exception
    }

    // Visitor Methods ---------------------------------------------------------------------

    public String visit(IRBinOp b) {
        // Uses %rax to operate on things. Returns %rax (sometimes %rdx)
        
        //Autospill temp onto stack
        String right = b.right.accept(this);
        cmds.add(String.format("movq %s, %%rax", right));
        right = String.format("-%d(%%rbp)", genTemp());
        cmds.add(String.format("movq %%rax, %s", right));
        
        String left = b.left.accept(this);
        cmds.add(String.format("movq %s, %%rax", left));
        
        switch(b.type) {
            case ADD:
                cmds.add(String.format("addq %s, %%rax", right));
                return "%rax";
            case SUB:
                cmds.add(String.format("subq %s, %%rax", right));
                return "%rax";
            case MUL:
                // TODO do we need to deal with rdx overwrite??
                cmds.add(String.format("movq %s, %%rdx", right));
                cmds.add("imulq %rdx");
                return "%rax";
            case HMUL:
                // TODO do we need to deal with rdx overwrite??
            cmds.add(String.format("movq %s, %%rdx", right));
                cmds.add("imulq %rdx");
                return "%rdx";
            case DIV:
                cmds.add("cqo");   // sign-extend %rax into %rdx TODO Check this is right
                cmds.add(String.format("idivq %s", right));
                return "%rax";
            case MOD:
                cmds.add("cqo");   // sign-extend %rax into %rdx TODO Check this is right
                cmds.add(String.format("idivq %s", right));
                return "%rdx"; 
            case AND:
                cmds.add(String.format("andq %s, %%rax", right));   //TODO check if it should be quadword
                return "%rax";
            case OR:
                cmds.add(String.format("orq %s, %%rax", right));    //TODO check if it should be quadword
                return "%rax";
            case XOR:
                cmds.add(String.format("xorq %s, %%rax", right));   //TODO check if it should be quadword
                return "%rax";
            case LSHIFT:
                cmds.add(String.format("shlq %s, %%rax", right));   //TODO need to guarantee right is an immediate or cl
                return "%rax";
            case RSHIFT:
                cmds.add(String.format("shrq %s, %%rax", right));   //TODO need to guarantee right is an immediate or cl
                return "%rax";
            case ARSHIFT:
                cmds.add(String.format("sarq %s, %%rax", right));   //TODO need to guarantee right is an immediate or cl
                return "%rax";
            case EQ:
                cmds.add(String.format("cmpq %s, %%rax", right));
                cmds.add("sete %al");  //set lower bits of %rax to 1 if equal
                return "%rax";
            case NEQ:
                cmds.add(String.format("cmpq %s, %%rax", right));   //TODO check if it should be quadword
                cmds.add("setne %al");
                return "%rax";
            case LT:
                cmds.add(String.format("cmpq %s, %%rax", right));   //TODO check if it should be quadword
                cmds.add("setl %al");
                return "%rax";
            case GT:
                cmds.add(String.format("cmpq %s, %%rax", right));   //TODO check if it should be quadword
                cmds.add("setg %al");
                return "%rax";
            case LEQ:
                cmds.add(String.format("cmpq %s, %%rax", right));   //TODO check if it should be quadword
                cmds.add("setle %al");
                return "%rax";
            case GEQ:
                cmds.add(String.format("cmpq %s, %%rax", right));   //TODO check if it should be quadword
                cmds.add("setge %al");
                return "%rax";
        }

        // These cases should be exhaustive
        assert false;
        return null;
    }
    
    public String visit(IRCall c) {
        List<IRNode> args = new ArrayList<>(c.args);
        int i;
        int memLoc=-1;
        int isMultipleReturn=0; //A pseudohack to shift by 1 in the case of multiple returns

        // TODO CHECK THIS Can you call anything other than an IRName?
        int numReturn = numReturn(((IRName) c.target).name);

        // Need to pass in mem address into ARG1()
        if(numReturn > 2) {
            memLoc = args.size()+1-6+numReturn-2;   //+1 for adding mem address, -6 for 6 arg registers, -2 for 2 return registers
            isMultipleReturn = 1;
        }

        // Used to help setup stack pointer
        if(numReturn - 2> maxReturn) {
            maxReturn = numReturn-2;
        }
        if(args.size()+isMultipleReturn - 6 > maxArgs) {
            maxArgs = args.size()+isMultipleReturn-6;
        }

        // Push any argument above 6 onto the stack
        // TODO Uses %rax, is this safe?
        for(i=args.size()-1;i>5-isMultipleReturn;i--) {
            cmds.add(String.format("movq %s, %%rax",args.get(i).accept(this)));
            cmds.add(String.format("movq %%rax, %d(%%rsp)", (i-6+isMultipleReturn)*8)); //-6 for 6 arguments, +isMultipleReturn for extra memory argument
        }

        // Assign all arguments 6 or below into the appropriate register
        // Their arguments are 1-indexed. (Why... T_T)
        for(;i>=0;i--) {
            cmds.add(String.format("movq %s, ARG%d()",args.get(i).accept(this),i+1+isMultipleReturn));
        }

        if(isMultipleReturn == 1) {
            if(memLoc == -1) {
                throw new RuntimeException("IRCall: How did you get here?");
            }
            cmds.add(String.format("leaq %d(%%rsp), ARG1()", memLoc*8));
        }

        // TODO CHECK THIS Can you call anything other than an IRName?
        cmds.add("callq FUNC("+((IRName) c.target).name.substring(1)+")");

        return "%rax";
    }

    public String visit(IRCJump c) {
        // TODO do we want to fold comparison operators into tile? Or assume it's always a temp?
        // TODO Potentially add more tiles here
        String condTemp = c.cond.accept(this);
        cmds.add(String.format("movq %s, %%rax", condTemp)); // TODO only needed if condTemp is an immediate
        cmds.add("cmpq $1, %rax");  // TODO Is this correct? Should we comparing %al instead?
        cmds.add("jz "+c.trueLabel);

        return null;
    }

    public String visit(IRJump j) {
        String label = ((IRName) j.target).name;
        cmds.add("jmp "+label);
        return null;
    }
    
    public String visit(IRCompUnit c) {
        cmds.add("#include \"defs.h\"");
        cmds.add(".text");
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        return null;
    }

    public String visit(IRConst c) {
        return "$"+Long.toString(c.value);
    }

    public String visit(IRESeq e) {
        throw new RuntimeException("IRESeq: How did you get here?");
    }

    public String visit(IRExp e) {
        throw new RuntimeException("IRExp: How did you get here?");        
    }

    public String visit(IRFuncDecl f) {
        tempCounter = 0;
        maxArgs = 0;
        maxReturn = 0;
        isMultipleReturn = 0;
        returnLoc = 0;
        namedTemps.clear();
        fnName = f.name;

        if(numReturn(f.name) > 2) {
            isMultipleReturn = 1;
        }

        // Prelude
        cmds.add(".globl "+"FUNC("+f.name.substring(1)+")");
        cmds.add(".align 4");
        cmds.add("FUNC("+f.name.substring(1)+"):");
        
        // Set up stack
        cmds.add("# Stack Setup");  // TODO Debugging comment
        cmds.add("pushq %rbp");
        cmds.add("movq %rsp, %rbp");
        cmds.add("subq tempCounter, %rsp: REPLACE THIS"); //Placeholder text
        int replaceIndex = cmds.size()-1;

        if(isMultipleReturn == 1) {
            returnLoc = genTemp();
            cmds.add(String.format("movq ARG1(), -%d(%%rbp)", returnLoc));
        }
        
        f.body.accept(this); // The IR moves arguments off registers onto stack. See visit(IRTemp)

        // Need to shift rsp
        int rspShift = tempCounter + maxArgs*8 + maxReturn*8;
        if (rspShift%16 == 8) {
            rspShift += 8;
        }
        cmds.set(replaceIndex, String.format("subq $%d, %%rsp", rspShift));

        //Tear down stack
        cmds.add("\n# Stack Teardown");   // TODO Debugging comment
        cmds.add("ret__label"+fnName+":");    //TODO Is this too hacky?
        cmds.add(String.format("addq $%d, %%rsp", rspShift));
        cmds.add("popq %rbp");
        cmds.add("retq");
        cmds.add("");   //New Line
        cmds.add("################################################################################");   //For style

        return null;
    }

    public String visit(IRLabel l) {
        cmds.add(l.name()+":");
        return null;
    }

    public String visit(IRMem m) {
        // TODO Uses %rax to move things around; is this safe? (Should be)
        cmds.add(String.format("movq %s, %%rax", m.expr.accept(this))); // Exprs shouldn't return null
        return "(%rax)";
    }

    public String visit(IRMove m) {
        // TODO Uses %r12 to move things around; is this safe? (Must use rax because ARGS use rdi, rsi, rdx, rcx, r8, and r9)
        // TODO Add more tiles here
        String r12 = String.format("-%d(%%rbp)",genTemp());
        cmds.add(String.format("movq %%r12, %s",r12));
        String src = m.src.accept(this);
        cmds.add(String.format("movq %s, %%r12", src));
        
        String target = m.target.accept(this);
        cmds.add(String.format("movq %%r12, %s", target));
        cmds.add(String.format("movq %s, %%r12", r12));
        return null;
    }

    public String visit(IRName n) {
        throw new RuntimeException("IRName: You don't need to be here");
    }

    public String visit(IRReturn r) {
        // For multiple returns (>2)
        // Uses %rax, %rdx to move data around. Safe because it must use %rax, %rdx
        for(int i=2;i<r.rets.size();i++) {
            String fromLoc = r.rets.get(i).accept(this);
            cmds.add(String.format("movq %s, %%rax", fromLoc));
            cmds.add(String.format("movq -%d(%%rbp), %%rdx",returnLoc));    //TODO Something is wrong here but idk what
            cmds.add(String.format("movq %%rax, -%d(%%rdx)",(i-2)*8));
        }

        // First two returns
        if(1<r.rets.size()) {
            cmds.add(String.format("movq %s, %%rdx", r.rets.get(1).accept(this)));
        }
        if(0<r.rets.size()) {
            cmds.add(String.format("movq %s, %%rax", r.rets.get(0).accept(this)));
        }

        cmds.add("jmp ret__label"+fnName); //TODO is this too hacky?

        return null;
    }

    public String visit(IRSeq s) {
        int i = 0;
        for(IRNode stmt:s.stmts) {
            cmds.add("\n# IR Statement "+i);   // TODO Debugging comment
            i++;
            stmt.accept(this);
        }
        return null;
    }

    public String visit(IRTemp t) {
        String name = t.name;

        // Handling special named temps (_ARG and _RET)
        // Shift by one if number of returns > 2
        if(name.length() > 3 && name.substring(0,4).equals("_ARG")) {
            int i = Integer.parseInt(name.substring(4))+1+isMultipleReturn;  //+1 to 1-index
            if(i <= 6) {
                return String.format("ARG%d()", i);
            }
            return String.format("%d(%%rbp)", (i-6+1)*8);  // -6 for 6 args, +1 to move above rbp
        }
        else if(name.length() > 3 && name.substring(0,4).equals("_RET")) {
            int i = Integer.parseInt(name.substring(4));
            if(i==0) {
                return "%rax";
            }
            else if(i==1) {
                return "%rdx";
            }
            else {
                // TODO Uses %rax, is this safe?
                cmds.add(String.format("movq -%d(%%rbp), %%rax",returnLoc));
                return String.format("-%d(%%rax)", (i-1)*8);
            }
        }

        // Generic named temps
        if(!namedTemps.containsKey(name)) {
            namedTemps.put(name,genTemp());
        } 
        return String.format("-%d(%%rbp)",namedTemps.get(name));
    }
}
