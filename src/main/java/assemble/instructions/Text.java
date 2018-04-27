package assemble.instructions;

import assemble.*;

public abstract class Text<A> extends Instr<A> {

    protected String text;

    // public static Text comment(String c) {
    //     return new Text("# " + c);
    // }

    // public static Text text(String t) {
    //     return new Text(t);
    // }

    // public static Text label(String l) {
    //     return new Text(l + ":");
    // }

    private Text(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static class T extends Text<Temp> {
        public T(String text) { super(text); }
        public <T> T accept(InsVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Text<Reg> {
        public R(String text) { super(text); }
    }
}
