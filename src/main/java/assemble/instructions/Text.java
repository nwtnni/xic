package assemble.instructions;

import java.util.List;
import java.util.Arrays;

public class Text extends Instr {
    protected String text;

    public static Text comment(String c) {
        return new Text("# " + c);
    }

    public static Text text(String t) {
        return new Text(t);
    }

    public static Text label(String l) {
        return new Text(l + ":");
    }

    private Text(String text) {
        this.text = text;
    }
    
    @Override
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(text);
    }
}