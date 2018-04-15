package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import assemble.*;
import ir.IRFuncDecl;

public class FuncDecl {
    public String name;
    public int args;
    public int rets;
    public Label returnLabel;

    public List<Instr> prelude;
    public List<Instr> stmts;
    public List<Instr> epilogue;

    public FuncDecl(IRFuncDecl fn, int args, int rets, List<Instr> stmts) {
        this.name = fn.name;
        this.args = args;
        this.rets = rets;
        this.returnLabel = Label.retLabel(fn);
        this.stmts = stmts;

        // Function prelude
        prelude = new ArrayList<>();
        prelude.add(Text.text("################################################################################"));
        prelude.add(Text.text(".globl " + name));
        prelude.add(Text.text(".align 4"));
        prelude.add(Label.label(fn));
        
        prelude.add(Text.comment("Stack Setup"));
        prelude.add(new Push(Operand.RBP));
        prelude.add(new Mov(Operand.RBP, Operand.RSP));
        prelude.add(Text.comment("~~~Replace with subtract from %rsp here"));
        // In reg alloc insert subq n, %rsp at prelude[7]

        // If function has multiple returns, save return address from arg 0 to a temp
        if (rets > 2) {
            args++;
            prelude.add(Text.comment("~~~Replace move from arg0 to calleeReturnAddr temp"));
            // In reg alloc handle multiple return addr at prelude[8]
        }
        prelude.add(Text.text(""));

        // Function epilogue
        epilogue = new ArrayList<>();
        epilogue.add(Text.comment("Stack Teardown"));
        epilogue.add(returnLabel);
        epilogue.add(Text.comment("~~~Replace with add to %rsp here:"));
        // In reg alloc insert addq n, %rsp at epiloque[2]
        epilogue.add(new Pop(Operand.RBP));
        epilogue.add(new Ret());
        epilogue.add(Text.text(""));
    }
    
    public FuncDecl(String name, List<Instr> prelude, List<Instr> stmts, List<Instr> epilogue) {
        this.name = name;
        this.prelude = prelude;
        this.stmts = stmts;
        this.epilogue = epilogue;
    }
    
    public List<String> toAbstractAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr i : prelude) {
            instrs.addAll(i.toAbstractAssembly());
        }
        for (Instr i : stmts) {
            instrs.addAll(i.toAbstractAssembly());
        }
        for (Instr i : epilogue) {
            instrs.addAll(i.toAbstractAssembly());
        }
        return instrs;
    }

    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr i : prelude) {
            instrs.addAll(i.toAssembly());
        }
        for (Instr i : stmts) {
            instrs.addAll(i.toAssembly());
        }
        for (Instr i : epilogue) {
            instrs.addAll(i.toAssembly());
        }
        return instrs;
    }
}