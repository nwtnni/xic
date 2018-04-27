package assemble.instructions;

import assemble.*;
import ir.IRFuncDecl;
import ir.IRLabel;

public abstract class Label<A> extends Instr<A> {

    protected String name;

    // /**
    //  * Generate a label from an IRLabel
    //  */
    // public static <T> Label<T> label(IRLabel l) {
    //     return new Label<>(l.name() + ":");
    // }

    // /**
    //  * Generate a label from an IRLabel
    //  */
    // public static <T> Label<T> funLabel(IRFuncDecl fn) {
    //     return new Label<>(fn.name()+ ":");
    // }

    // /**
    //  * Generate a label from an IRFuncDecl 
    //  */
    // public static <T> Label<T> retLabel(IRFuncDecl fn) {
    //     return new Label<>("_RET_" + fn.name() + ":");
    // }

    private Label(String name) {
        this.name = name;
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
        public <T> T accept(InsVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Label<Reg> {
        public R(String name) { super(name); }
    }
}
