package assemble.instructions;

import assemble.*;
import ir.IRFuncDecl;
import ir.IRLabel;

public abstract class Label<A> extends Instr<A> {

    protected String name;

    private Label(String name) {
        this.name = name;
    }

    private Label(IRLabel l) {
        this.name = l.name() + ":";
    }

    public String name() {
        return name.substring(0, name.length() - 1);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * All labels with same name are equal.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Label && name.equals(((Label) o).name);
    }

    @Override
    public String toString() {
        return name;
    }

    public static class T extends Label<Temp> {
        public T(String name) { super(name); }
        public T(IRLabel label) { super(label); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Label<Reg> {
        public R(String name) { super(name); }
        public R(IRLabel label) { super(label); }
    }
}
