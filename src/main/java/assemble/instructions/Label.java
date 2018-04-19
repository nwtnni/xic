package assemble.instructions;

import java.util.List;

import ir.IRFuncDecl;
import ir.IRLabel;

import java.util.Arrays;

public class Label extends Instr {
    protected String name;

    /**
     * Generate a label from an IRLabel
     */
    public static Label label(IRLabel l) {
        return new Label(l.name + ":");
    }

    /**
     * Generate a label from an IRLabel
     */
    public static Label label(IRFuncDecl fn) {
        return new Label(fn.name + ":");
    }

    /**
     * Generate a label from an IRFuncDecl 
     */
    public static Label retLabel(IRFuncDecl f) {
        return new Label("_RET_" + f.name + ":");
    }

    private Label(String name) {
        this.name = name;
    }

    public String name() {
        return name.substring(0, name.length() - 1);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(name);
    }
}