package assemble.instructions;

import assemble.*;

import ir.IRJump;

public abstract class Jmp<A> extends Instr<A> {

    public Label<A> label;

    private Jmp(Label<A> label) {
        this.label = label;
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
        public T(IRJump jump) { super(new Label.T(jump.targetLabel())); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Jmp<Reg> {
        public R(Label<Temp> label) { super(new Label.R(label.name())); }
        public R(IRJump jump) { super(new Label.R(jump.targetLabel())); }
    }
}
