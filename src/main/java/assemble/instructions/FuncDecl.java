package assemble.instructions;

import java.util.List;
import java.util.ArrayList;

import assemble.*;
import static assemble.Operand.Kind.*;

public class FuncDecl {
    public String name;

    public List<Instr> prelude;
    public List<Instr> stmts;
    public List<Instr> epilogue;

    public FuncDecl(String name, List<Instr> stmts) {
        this.name = name;
        this.stmts = stmts;

        // Function prelude
        prelude = new ArrayList<>();
        prelude.add(Text.comment("################################################################################"));
        prelude.add(Text.text(".globl " + name));
        prelude.add(Text.text(".align 4"));
        prelude.add(Label.label(name));
        
        prelude.add(Text.comment("Stack Setup"));
        prelude.add(new Push(Operand.reg(RBP)));
        prelude.add(new Mov(Operand.reg(RSP), Operand.reg(RBP)));
        prelude.add(Text.comment("Subtract from %rsp here:"));
        // TODO: in reg alloc insert subq n, %rsp at prelude[4]

        // Function epilogue
        epilogue.add(Text.comment("Stack Teardown"));
        epilogue.add(Label.retLabel(name));
        epilogue.add(Text.comment("Add to %rsp here:"));
        // TODO: in reg alloc insert addq n, %rsp at epiloque[2]
        epilogue.add(new Pop(Operand.reg(RBP)));
        epilogue.add(new Ret());
        epilogue.add(Text.comment(""));
    }
    
    public FuncDecl(String name, List<Instr> prelude, List<Instr> stmts, List<Instr> epilogue) {
        this.name = name;
        this.prelude = prelude;
        this.stmts = stmts;
        this.epilogue = epilogue;
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