package assemble;

import ir.*;
import type.FnContext;
import interpret.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Currently hardcoded in AT&T syntax, add factory methods instead of dealing with pure strings
// TODO Currently hardcoded all ARG1() and _ARG1 calls

public class Assembler extends IRVisitor<String> {
    
    /**
     * Returns the assembly code given a canonical IR AST
     *
     */
    public static String assemble(IRNode ast) {
        Assembler assembler = new Assembler();
        ast.accept(assembler);
        return null;
    }

    /**
     * The running list of assembly commands
     */
    public List<String> cmds;                       // Assembly Code
    private int tempCounter = 0;                    // How many temps are used
    private HashMap<IRTemp, Integer> namedTemps;    // Translates IRTemp to memory offset
    private int maxReturn = 0;
    private int maxArgs = 0;
    private int isMultipleReturn;
    private int returnLoc;

    /**
     * Constructor initializes @param cmds.
     */
    private Assembler() {
        cmds = new ArrayList<String>();
        namedTemps = new HashMap<>();
    }

    private int genTemp() {
        tempCounter -= 8;
        return tempCounter;
    }

    // TODO This may be slightly hacky
    private int numReturn(String fn) {
        fn = fn.substring(fn.lastIndexOf("_"));
        if(fn.charAt(0) == 'p'){
            return 0;
        }
        else if (fn.charAt(0) == 'i' || fn.charAt(0) == 'b') {
            return 1;
        }
        else if (fn.charAt(0) == 't') {
            fn = fn.replaceAll("[^\\d]", "");    //TODO Verify that this actually works
            return Integer.parseInt(fn);
        }
        throw new RuntimeException("This is an invalid ABI name."); //TODO Fix exception
    }

    // TODO
    private int numArgs(String fn) {
        throw new RuntimeException("IMPLEMENT");
    }

    // Visitor Methods ---------------------------------------------------------------------

    public String visit(IRBinOp b) {
        // Uses %rax to operate on things. Returns %rax (sometimes %rdx)
        String left = b.left.accept(this);
        String right = b.right.accept(this);
        cmds.add(String.format("movq %s, %%rax", left));
        switch(b.type) {
            case ADD:
                cmds.add(String.format("addq %s, %%rax", right));
            case SUB:
                cmds.add(String.format("subq %s, %%rax", right));
            case MUL:
                // TODO do we need to deal with rdx overwrite??
                cmds.add(String.format("imulq %s", right));
            case HMUL:
                // TODO do we need to deal with rdx overwrite??
                cmds.add(String.format("imulq %s", right));
                return "%rdx";
            case DIV:
                cmds.add("cdqo");   // sign-extend %rax into %rdx TODO Check this is right
                cmds.add(String.format("idivq %s", right));
            case MOD:
                cmds.add("cdqo");   // sign-extend %rax into %rdx TODO Check this is right
                cmds.add(String.format("idivq %s", right));
                return "%rdx"; 
            case AND:
                cmds.add(String.format("andb %s, %%rax", right));
            case OR:
                cmds.add(String.format("orb %s, %%rax", right));
            case XOR:
                cmds.add(String.format("xorb %s, %%rax", right));
            case LSHIFT:
                cmds.add(String.format("shlq %s, %%rax", right));   //TODO need to guarantee right is an immediate or cl
            case RSHIFT:
                cmds.add(String.format("shrq %s, %%rax", right));   //TODO need to guarantee right is an immediate or cl
            case ARSHIFT:
                cmds.add(String.format("sarq %s, %%rax", right));   //TODO need to guarantee right is an immediate or cl
            case EQ:
                cmds.add(String.format("cmpq %s, %%rax", right));
                cmds.add("sete %al");  //set lower bits of %rax to 1 if equal
            case NEQ:
                cmds.add(String.format("cmpq %s, %%rax", right));
                cmds.add("setne %al");
            case LT:
                cmds.add(String.format("cmpq %s, %%rax", right));
                cmds.add("setl %al");
            case GT:
                cmds.add(String.format("cmpq %s, %%rax", right));
                cmds.add("setg %al");
            case LEQ:
                cmds.add(String.format("cmpq %s, %%rax", right));
                cmds.add("setle %al");
            case GEQ:
                cmds.add(String.format("cmpq %s, %%rax", right));
                cmds.add("setge %al");
        }

        return "%rax";
    }
    
    public String visit(IRCall c) {
        List<IRNode> args = new ArrayList<>(c.args);
        int i;
        int memLoc=-1;
        int isMultipleReturn=0; //A pseudohack to shift by 1 in the case of multiple returns

        // TODO CHECK THIS Can you call anything other than an IRName?
        int numReturn = numReturn(((IRName) c.target).name);

        // Need to pass in mem address into ARG1()
        if(numReturn > 3) {
            memLoc = args.size()+1-6+numReturn;   //+1 for adding mem address, -6 for 6 registers
            isMultipleReturn = 1;
        }

        // Used to help setup stack pointer
        if(numReturn > maxReturn) {
            maxReturn = numReturn;
        }
        if(args.size() > maxArgs) {
            maxArgs = args.size();
        }

        // Push any argument above 6 onto the stack
        for(i=args.size()-1;i>5-isMultipleReturn;i--) {
            cmds.add(String.format("movq -%d(%%rbp),%d(%%rsp)",args.get(i).accept(this),(i+1)*8));
        }

        // Assign all arguments 6 or below into the appropriate register
        // Their arguments are 1-indexed. (Why... T_T)
        while(i>=0) {
            cmds.add(String.format("movq -%d(%%rbp), ARG%d()",args.get(i).accept(this),i+1+isMultipleReturn));
        }

        if(isMultipleReturn == 1) {
            if(memLoc == -1) {
                throw new RuntimeException("IRCall: How did you get here?");
            }
            cmds.add(String.format("leaq %d(%%rsp), ARG1()", memLoc*8));
        }

        // TODO CHECK THIS Can you call anything other than an IRName?
        cmds.add("callq FUNC("+((IRName) c.target).name+")");

        return "%rax";
    }

    public String visit(IRCJump c) {
        // TODO do we want to fold comparison operators into tile? Or assume it's always a temp?
        String condTemp = c.cond.accept(this);
        cmds.add(String.format("cmpq $1, %s", condTemp));
        cmds.add("jnz "+c.trueLabel);

        return null;
    }

    public String visit(IRJump j) {
        String label = ((IRName) j.target).name;
        cmds.add("jmp "+label);
        return null;
    }
    
    public String visit(IRCompUnit c) {
        cmds.add(".text");
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        return null;
    }

    public String visit(IRConst c) {
        //TODO Is this actually needed?
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

        if(numReturn(f.name) > 2) {
            isMultipleReturn = 1;
        }

        // Prelude
        cmds.add(".globl "+f.name);
        cmds.add(".align 4");
        cmds.add("FUNC("+f.name+"):");
        
        // Set up stack
        cmds.add("pushq %rbp");
        cmds.add("movq %rsp, %rbp");
        cmds.add("subq tempCounter, %rsp: REPLACE THIS"); //Placeholder text
        int replaceIndex = cmds.size()-1;

        if(isMultipleReturn == 1) {
            returnLoc = genTemp();
            cmds.add(String.format("movq ARG1(), -%d(%%rbp)", returnLoc));
        }
        
        f.body.accept(this); // The IR moves arguments off registers onto stack. See visit(IRMove) below

        // Need to shift rsp
        int rspShift = tempCounter + maxArgs + maxReturn;
        if (rspShift%2 == 1) {
            rspShift += 1;
        }
        cmds.set(replaceIndex, String.format("subq $%d, %%rsp", rspShift));

        //Tear down stack
        cmds.add("addq $%d, %rsp");
        cmds.add("popq %rbp");
        cmds.add("retq");
        cmds.add("");   //New Line

        return null;
    }

    public String visit(IRLabel l) {
        cmds.add(l.name()+":");
        return null;
    }

    public String visit(IRMem m) {
        return "("+m.expr.accept(this)+")"; // Exprs shouldn't return null
    }

    public String visit(IRMove m) {
        // TODO Uses %rax to move things around; is this safe?
        String target = m.target.accept(this);
        String src = m.src.accept(this);
        cmds.add(String.format("movq %s, %%rax", src));
        cmds.add(String.format("movq %%rax, %s", target));
        return null;
    }

    public String visit(IRName n) {
        throw new RuntimeException("IRName: You don't need to be here");
    }

    public String visit(IRReturn r) {
        // For multiple returns (>2)
        // Uses %rax to move data around. Safe because it must use %rax, %rdx
        for(int i=2;i<r.rets.size();i++) {
            String fromLoc = r.rets.get(i).accept(this);
            cmds.add(String.format("movq %s, %%rax", fromLoc));

            // TODO Not sure if valid assembly. Is this too dense?
            cmds.add(String.format("movq %%rax, -%d(-%d(%%rbp))", (i-2)*8 ,returnLoc*8));
        }

        // First two returns
        if(0<r.rets.size()) {
            cmds.add(String.format("movq -%d(%rbp), %%rax", r.rets.get(0).accept(this)));
        }
        if(1<r.rets.size()) {
            cmds.add(String.format("movq -%d(%rbp), %%rdx", r.rets.get(1).accept(this)));
        }

        return null;
    }

    public String visit(IRSeq s) {
        for(IRNode stmt:s.stmts) {
            stmt.accept(this);
        }
        return null;
    }

    public String visit(IRTemp t) {
        String name = t.name;

        // Handling special named temps (_ARG and _RET)
        // Shift by one if number of returns > 2
        if(name.substring(0,4).equals("_ARG")) {
            int i = Integer.parseInt(name.substring(4))+1+isMultipleReturn;  //+1 to 1-index
            if(i <= 6) {
                return String.format("ARG%d()", i);
            }
            return String.format("+%d(%%rbp)", (i-6+1)*8);  // -6 for 6 args, +1 to move above rbp
        }
        else if(name.substring(0,4).equals("_RET")) {
            int i = Integer.parseInt(name.substring(4))+1; //+1 to 1-index
            if(i==1) {
                return "%rax";
            }
            else if(i==2) {
                return "%rdx";
            }
            else {
                // TODO is this valid assembly? Not sure if too dense
                return String.format("-%d(-%d(%%rbp))",(i-2)*8,returnLoc*8);
            }
        }

        // Generic named temps
        if(!namedTemps.containsKey(t)) {
            namedTemps.put(t,genTemp());
        } 
        return String.format("-%d(%%rbp)",namedTemps.get(t));
    }
}
