package assemble;

import java.util.List;
import java.util.ArrayList;

import assemble.instructions.*;
import ir.IRFuncDecl;

public class FuncDecl {
    public String sourceName;
    public String name;
    public int args;
    public int rets;
    public Label returnLabel;

    public List<Instr> prelude;
    public List<Instr> stmts;
    public List<Instr> epilogue;

    public FuncDecl(IRFuncDecl fn, int args, int rets, List<Instr> stmts) {
        this.sourceName = fn.sourceName();
        this.name = fn.name();
        this.args = args;
        this.rets = rets;
        this.returnLabel = Label.retLabel(fn);

        // Function prelude
        prelude = new ArrayList<>();
        prelude.add(Text.text("################################################################################"));
        prelude.add(Text.text(".globl " + name));
        prelude.add(Text.text(".align 4"));
        prelude.add(Label.funLabel(fn));
        
        prelude.add(Text.comment("Stack Setup"));
        prelude.add(new Push(Operand.RBP));
        prelude.add(new Mov(Operand.RBP, Operand.RSP));

        

        prelude.add(Text.comment("~~~Replace with subtract from %rsp here"));

        this.stmts = stmts;

        // Function epilogue
        epilogue = new ArrayList<>();
        epilogue.add(returnLabel);
        epilogue.add(Text.comment("Stack Teardown"));
        epilogue.add(Text.comment("~~~Replace with add to %rsp here:"));
        epilogue.add(new Pop(Operand.RBP));
        epilogue.add(new Ret());
        epilogue.add(Text.text(""));
    }
    
    public FuncDecl(FuncDecl fn) {
        this.sourceName = fn.sourceName;
        this.name = fn.name;
        this.args = fn.args;
        this.rets = fn.rets;
        this.returnLabel = fn.returnLabel;
        this.prelude = fn.prelude;
        this.stmts = fn.stmts;
        this.epilogue = fn.epilogue;
    }

    public void setStackSize(int i) {
        Operand shift = Operand.imm(Config.WORD_SIZE * i);

        // Insert stack setup at prelude[7]
        BinOp sub = new BinOp(BinOp.Kind.SUB, Operand.RSP, shift);
        prelude.set(prelude.size() - 1, sub);

        // Insert stack teardown at epilogue[2]
        BinOp add = new BinOp(BinOp.Kind.ADD, Operand.RSP, shift);
        epilogue.set(2, add);
    }

    public void saveRegister(Operand reg) {
        assert reg.isReg();
        prelude.add(prelude.size() - 1, new Push(reg));
        epilogue.add(3, new Pop(reg));
    }

    
    public List<String> toAbstractAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr i : prelude) {
            instrs.add(i.toAbstractAssembly());
        }
        for (Instr i : stmts) {
            instrs.add(i.toAbstractAssembly());
        }
        for (Instr i : epilogue) {
            instrs.add(i.toAbstractAssembly());
        }
        return instrs;
    }

    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr i : prelude) {
            instrs.add(i.toAssembly());
        }
        for (Instr i : stmts) {
            instrs.add(i.toAssembly());
        }
        for (Instr i : epilogue) {
            instrs.add(i.toAssembly());
        }
        return instrs;
    }
}
