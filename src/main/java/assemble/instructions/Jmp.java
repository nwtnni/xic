package assemble.instructions;

import ir.IRJump;

public class Jmp extends Instr {

    public static Jmp fromJmp(IRJump j) {
        return new Jmp(Label.label(j.targetLabel()));
    }

    public static Jmp toLabel(Label l) {
        return new Jmp(l);
    }

    public Label label;

    // TODO: only support jump to label for now
    private Jmp(Label label) {
        this.label = label;
    }

    public boolean hasLabel() {
        return label != null;
    }

    @Override
    public String toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public String toAssembly() {
        return "jmp " + label.name();
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}