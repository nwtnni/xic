package assemble.instructions;

import assemble.*;

import ir.IRJump;

public abstract class Jmp<A> extends Instr<A> {

    public Label<A> label;

    private Jmp(Label<A> label) {
        this.label = label;
    }

    private Jmp(IRJump jump) {
        this.label = Label.label(jump.targetLabel());
    }

    public boolean hasLabel() {
        return label != null;
    }

    @Override
    public String toString() {
        return "jmp " + label.name();
    }

    public static class T extends Jmp<Temp> {
        public T(Label<Temp> label) { super(label); }
        public T(IRJump jump) { super(jump); }
        public <T> T accept(InsVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Jmp<Reg> {
        public R(Label<Reg> label) { super(label); }
        public R(IRJump jump) { super(jump); }
    }
}
