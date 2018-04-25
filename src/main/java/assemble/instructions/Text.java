package assemble.instructions;

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
    public String toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public String toAssembly() {
        return text;
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}