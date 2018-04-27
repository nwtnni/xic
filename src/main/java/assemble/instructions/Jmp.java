package assemble.instructions;

import assemble.*;

import ir.IRJump;

public class Jmp<A> extends Instr<A> {

    public static <T> Jmp<T> of(Label<T> label) {
        return new Jmp<>(label);
    }

    public static <T> Jmp<T> of(IRJump jump) {
        return new Jmp<>(jump);
    }

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

    @Override
    public <T> T accept(InsVisitor<A, T> v) {
        return v.visit(this);
    }
}
