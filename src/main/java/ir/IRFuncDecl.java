package ir;

/** An IR function declaration */
public class IRFuncDecl extends IRNode {
    private String name;
    private IRNode body;

    public IRFuncDecl(String name, IRNode body) {
        this.name = name;
        this.body = body;
    }

    public String name() {
        return name;
    }

    public IRNode body() {
        return body;
    }

    @Override
    public String label() {
        return "FUNC " + name;
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
