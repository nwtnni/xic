package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import assemble.*;
import static assemble.Operand.Kind.*;

public class FuncDecl {
    public String name;
    public int args;
    public int rets;

    public List<Instr> prelude;
    public List<Instr> stmts;
    public List<Instr> epilogue;

    public FuncDecl(String name, int args, int rets, List<Instr> stmts) {
        this.name = name;
        this.args = args;
        this.rets = rets;
        this.stmts = stmts;

        // Function prelude
        prelude = new ArrayList<>();
        prelude.add(Text.comment("################################################################################"));
        prelude.add(Text.text(".globl " + name));
        prelude.add(Text.text(".align 4"));
        prelude.add(Label.label(name));
        
        prelude.add(Text.comment("Stack Setup"));
        prelude.add(new Push(Operand.RBP));
        prelude.add(new Mov(Operand.RSP, Operand.RBP));
        prelude.add(Text.comment("Subtract from %rsp here:"));
        // TODO: in reg alloc insert subq n, %rsp at prelude[4]
        // TODO: in reg alloc handle multiple return addr at prelude[5]

        // If function has multiple returns, save return address from arg 0 to a temp
        if (rets > 2) {
            prelude.add(Text.comment("Callee multiple returns mem address here:"));
            prelude.add(new Mov(TempFactory.getArgument(0), Config.CALLEE_MULT_RETURN));
        }

        // Function epilogue
        epilogue = new ArrayList<>();
        epilogue.add(Text.comment("Stack Teardown"));
        epilogue.add(Label.retLabel(name));
        epilogue.add(Text.comment("Add to %rsp here:"));
        // TODO: in reg alloc insert addq n, %rsp at epiloque[2]
        epilogue.add(new Pop(Operand.RBP));
        epilogue.add(new Ret());
        epilogue.add(Text.comment(""));
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