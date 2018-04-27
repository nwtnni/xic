package assemble.instructions;

import assemble.*;

import ir.IRJump;

public class Jmp<A> extends Instr<A> {

    public Label label;

    private Jmp(Label label) {
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

    @Override
    public <T> T accept(InsVisitor<A, T> v) {
        return v.visit(this);
    }

    public static class T extends Jmp<Temp> {
        public T(Label label) { super(label); }
        public T(IRJump jump) { super(jump); }
    }

    public static class R extends Jmp<Reg> {
        public R(Label label) { super(label); }
        public R(IRJump jump) { super(jump); }
    }
}
