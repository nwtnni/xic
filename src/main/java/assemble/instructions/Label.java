package assemble.instructions;

import ir.IRFuncDecl;
import ir.IRLabel;

public class Label extends Instr {
    protected String name;

    /**
     * Generate a label from an IRLabel
     */
    public static Label label(IRLabel l) {
        return new Label(l.name() + ":");
    }

    /**
     * Generate a label from an IRLabel
     */
    public static Label funLabel(IRFuncDecl fn) {
        return new Label(fn.name()+ ":");
    }

    /**
     * Generate a label from an IRFuncDecl 
     */
    public static Label retLabel(IRFuncDecl fn) {
        return new Label("_RET_" + fn.name() + ":");
    }

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

    @Override
    public boolean equals(Object o) {
        return o instanceof Label && name.equals(((Label) o).name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public String toAssembly() {
        return name;
    }
    
    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}