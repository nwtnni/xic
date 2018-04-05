package assemble;

import ir.*;
import type.FnContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Currently hardcoded in AT&T syntax, add factory methods instead of dealing with pure strings

public class Assembler extends IRVisitor<Integer> {
    
    /**
     * Returns the assembly code given a canonical IR AST
     *
     */
    public static Integer assemble(IRNode ast) {
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

    // Visitor Methods ---------------------------------------------------------------------

    public Integer visit(IRBinOp b) {
        // TODO Multiple addressing modes and multiple binop types
        return null;
    }
    
    public Integer visit(IRCall c) {
        List<IRNode> args = new ArrayList<>(c.args);
        int i;
        int memLoc;
        int isMultipleReturn=0; //A pseudohack to shift by 1 in the case of multiple returns

        int numReturn = numReturn(((IRName) c.target).name);

        // Need to pass in mem address into ARG1()
        if(numReturn > 3) {
            memLoc = (args.size()+1-6)*8;   //+1 for adding mem address, -6 for 6 registers
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
            cmds.add(String.format("movq -%d(%%rbp), ARG1()"));
        }

        // TODO CHECK THIS Can you call anything other than an IRName?
        cmds.add("callq FUNC("+((IRName) c.target).name+")");
        return null;
    }

    public Integer visit(IRCJump c) {
        int tempLoc = c.cond.accept(this);
        cmds.add(String.format("cmpq $1, -%d(%%rbp)", tempLoc));
        cmds.add("jg "+c.trueLabel);

        return null;
    }

    public Integer visit(IRJump j) {
        String label = ((IRName) j.target).name;
        cmds.add("jmp "+label);
        return null;
    }
    
    public Integer visit(IRCompUnit c) {
        cmds.add(".text");
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        return null;
    }

    public Integer visit(IRConst c) {
        //TODO Not sure if this is needed
        throw new RuntimeException("IRConst: You don't need to be here. Probably?");
    }

    public Integer visit(IRESeq e) {
        throw new RuntimeException("IRESeq: How did you get here?");
    }

    public Integer visit(IRExp e) {
        throw new RuntimeException("IRExp: How did you get here?");        
    }

    public Integer visit(IRFuncDecl f) {
        tempCounter = 0;
        // Prelude
        cmds.add(".globl "+f.name);
        cmds.add(".align 4");
        cmds.add("FUNC("+f.name+"):");
        
        // Set up stack
        cmds.add("pushq %rbp");
        cmds.add("movq %rsp, %rbp");
        cmds.add("subq tempCounter, %rsp: REPLACE THIS"); //Placeholder text
        int replaceIndex = cmds.size()-1;

        f.body.accept(this);    // This already moves args off of registers onto the stack

        // Use tempCounter to shift rsp
        cmds.set(replaceIndex, String.format("subq $%d, %%rsp", replaceIndex));

        //Tear down stack
        cmds.add("addq $%d, %rsp");
        cmds.add("popq %rbp");
        cmds.add("retq");
        cmds.add("");   //New Line

        return null;
    }

    public Integer visit(IRLabel l) {
        cmds.add(l.name()+":");
        return null;
    }

    public Integer visit(IRMem m) {
        // TODO Multiple addressing modes
        return null;
    }

    public Integer visit(IRMove m) {
        // TODO Multiple addressing modes
        return null;
    }

    public Integer visit(IRName n) {
        throw new RuntimeException("IRName: You don't need to be here");
    }

    public Integer visit(IRReturn r) {
        // First two returns
        if(0<r.rets.size()) {
            cmds.add(String.format("-%d(%rbp), %%rax", r.rets.get(0).accept(this)));
        }
        if(1<r.rets.size()) {
            cmds.add(String.format("-%d(%rbp), %%rdx", r.rets.get(1).accept(this)));
        }

        // For multiple returns (>2)
        for(int i=2;i<r.rets.size();i++) {
            int fromLoc = r.rets.get(i).accept(this);
            int toLoc = namedTemps.get("_ARG1")+8*(i-2);
            cmds.add(String.format("-%d(%rbp), -%d(%rbp)", fromLoc, toLoc));
        }

        return null;
    }

    public Integer visit(IRSeq s) {
        for(IRNode stmt:s.stmts) {
            stmt.accept(this);
        }
        return null;
    }

    public Integer visit(IRTemp t) {
        if(!namedTemps.containsKey(t)) {
            namedTemps.put(t,genTemp());
        } 
        return namedTemps.get(t);
    }
}
