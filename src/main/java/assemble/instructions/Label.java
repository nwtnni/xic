package assemble.instructions;

import java.util.List;
import java.util.Arrays;

public class Label extends Instr {
    protected String text;

    public static Label label(String l) {
        return new Label(l + ":");
    }

    public static Label retLabel(String l) {
        return new Label("ret__" + l + ":");
    }

    private Label(String text) {
        this.text = text;
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(text);
    }
}