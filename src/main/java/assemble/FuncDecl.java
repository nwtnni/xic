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

        // Add directives
        instrs.add("# " + sourceName);
        instrs.add(".globl " + name);
        instrs.add(".align 4");

        // Insert prelude
        for (Instr<A> i : prelude) {
            String assembly = i.toString();
            if (!(i instanceof Label || i instanceof Text)) {
                assembly = "        " + assembly;
            }
            instrs.add(assembly);
        }

        // Insert function body
        for (Instr<A> i : stmts) {
            String assembly = i.toString();
            if (!(i instanceof Label || i instanceof Text)) {
                assembly = "        " + assembly;
            }
            instrs.add(assembly);
        }

        // Insert epilogue
        for (Instr<A> i : epilogue) {
            String assembly = i.toString();
            if (!(i instanceof Label || i instanceof Text)) {
                assembly = "        " + assembly;
            }
            instrs.add(assembly);
        }

        instrs.add("");

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
            prelude.add(labelFromFn(fn));
            prelude.add(pushR(Temp.RBP));
            prelude.add(movRR(Temp.RSP, Temp.RBP));
            prelude.add(comment("        Replace with subtract from %rsp here"));

            this.stmts = stmts;

            // Function epilogue
            epilogue = new ArrayList<>();
            epilogue.add(returnLabel);
            epilogue.add(comment("        Replace with add to %rsp here:"));
            epilogue.add(popR(Temp.RBP));
            epilogue.add(ret());
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
            prelude.add(new Label.R(fn.name + ":"));
            prelude.add(new Push.RR(Reg.RBP));
            prelude.add(new Mov.RRR(Reg.RSP, Reg.RBP));
            prelude.add(new Text.R("        # Replace with subtract from %rsp here"));

            // Function epilogue
            this.epilogue = new ArrayList<>();
            epilogue.add(returnLabel);
            epilogue.add(new Text.R("        # Replace with add to %rsp here:"));
            epilogue.add(new Pop.RR(Reg.RBP));
            epilogue.add(new Ret.R());
        }

        /** 
         * Shift the stack pointer by i words. 
         * (i is multiplyed by the word size in this function)
         */
        public void setStackSize(int i) {

            // Insert stack setup at end of prelude
            Imm shift = new Imm(Config.WORD_SIZE * i);
            BinOp<Imm, Reg, Reg> sub = new BinOp.RIR(BinOp.Kind.SUB, shift, Reg.RSP);
            prelude.set(prelude.size() - 1, sub);

            // Insert stack teardown at beginning of epilogue
            BinOp<Imm, Reg, Reg> add = new BinOp.RIR(BinOp.Kind.ADD, shift, Reg.RSP);
            epilogue.set(1, add);
        }

    }
}
