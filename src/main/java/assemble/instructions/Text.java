package assemble.instructions;

import assemble.*;

public abstract class Text<A> extends Instr<A> {

    public String text;

    private Text(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static class T extends Text<Temp> {
        public T(String text) { super(text); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Text<Reg> {
        public R(String text) { super(text); }
    }
}
