package assemble;

import java.util.List;
import java.util.ArrayList;

import assemble.instructions.*;
import static assemble.instructions.InstrFactory.*;
import ir.IRFuncDecl;

public abstract class FuncDecl<A> {

    public String sourceName;
    public String name;
    public int args;
    public int rets;
    public Label<A> returnLabel;

    public List<Instr<A>> prelude;
    public List<Instr<A>> stmts;
    public List<Instr<A>> epilogue;

    /*
     * Convert this function into its abstract assembly or assembly form.
     */
    public List<String> toAssembly() {
        List<String> instrs = new ArrayList<>();
        for (Instr<A> i : prelude) {
            instrs.add(i.toString());
        }
        for (Instr<A> i : stmts) {
            instrs.add(i.toString());
        }
        for (Instr<A> i : epilogue) {
            instrs.add(i.toString());
        }
        return instrs;
    }
    
    public static class T extends FuncDecl<Temp> {

        /*
        * Copy constructor
        */
        public static FuncDecl<Temp> copy(FuncDecl<Temp> fn) {
            T copy = new T();
            copy.sourceName = fn.sourceName;
            copy.name = fn.name;
            copy.args = fn.args;
            copy.rets = fn.rets;
            copy.returnLabel = fn.returnLabel;
            copy.prelude = fn.prelude;
            copy.stmts = fn.stmts;
            copy.epilogue = fn.epilogue;
            return copy;
        }

        private T() {}

        public T(IRFuncDecl fn, int args, int rets, List<Instr<Temp>> stmts) {
            this.sourceName = fn.sourceName();
            this.name = fn.name();
            this.args = args;
            this.rets = rets;
            this.returnLabel = labelFromRet(fn);

            // Function prelude
            prelude = new ArrayList<>();
            prelude.add(text("################################################################################"));
            prelude.add(text(".globl " + name));
            prelude.add(text(".align 4"));
            prelude.add(labelFromFn(fn));
            prelude.add(comment("Stack Setup"));
            prelude.add(pushR(Temp.RBP));
            prelude.add(movRR(Temp.RSP, Temp.RBP));
            prelude.add(comment("~~~Replace with subtract from %rsp here"));

            this.stmts = stmts;

            // Function epilogue
            epilogue = new ArrayList<>();
            epilogue.add(returnLabel);
            epilogue.add(comment("Stack Teardown"));
            epilogue.add(comment("~~~Replace with add to %rsp here:"));
            epilogue.add(popR(Temp.RBP));
            epilogue.add(ret());
            epilogue.add(text(""));
        }
        
        public void setStackSize(int i) {

            // Insert stack setup at end of prelude
            Imm shift = new Imm(Config.WORD_SIZE * i);
            BinOp<Imm, Temp, Temp> sub = binOpIR(BinOp.Kind.SUB, shift, Temp.RSP);
            prelude.set(prelude.size() - 1, sub);

            // Insert stack teardown at beginning of epilogue
            BinOp<Imm, Temp, Temp> add = binOpIR(BinOp.Kind.ADD, shift, Temp.RSP);
            epilogue.set(2, add);
        }

        public void saveRegister(Temp reg) {
            prelude.add(prelude.size() - 1, pushR(reg));
            epilogue.add(3, popR(reg));
        }
    }

    public static class R extends FuncDecl<Reg> {

        public R(FuncDecl<Temp> fn) {
            this.sourceName = fn.sourceName;
            this.name = fn.name;
            this.args = fn.args;
            this.rets = fn.rets;
            this.returnLabel = new Label.R(fn.returnLabel);
            this.stmts = new ArrayList<>(); 
            this.prelude = new ArrayList<>(); 
            prelude.add(new Text.R("##########################################################################"));
            prelude.add(new Text.R(".globl " + name));
            prelude.add(new Text.R(".align 4"));
            prelude.add(new Label.R(fn.name + ":"));
            prelude.add(new Text.R("# Stack Setup"));
            prelude.add(new Push.RR(Reg.RBP));
            prelude.add(new Mov.RRR(Reg.RSP, Reg.RBP));
            prelude.add(new Text.R("# ~~~Replace with subtract from %rsp here"));

            // Function epilogue
            this.epilogue = new ArrayList<>(); 
            epilogue.add(returnLabel);
            epilogue.add(new Text.R("# Stack Teardown"));
            epilogue.add(new Text.R("# ~~~Replace with add to %rsp here:"));
            epilogue.add(new Pop.RR(Reg.RBP));
            epilogue.add(new Ret.R());
            epilogue.add(new Text.R(""));
        }
    }
}
