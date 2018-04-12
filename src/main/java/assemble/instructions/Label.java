package assemble.instructions;

import java.util.List;
import java.util.Arrays;

public class Label extends Instr {
    protected String name;

    public static Label label(String l) {
        return new Label(l + ":");
    }

    public static Label retLabel(String l) {
        return new Label("ret__" + l + ":");
    }

    private Label(String name) {
        this.name = name;
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