package ir;

/**
 * An intermediate representation for named memory address
 * NAME(n)
 */
public class IRName extends IRExpr {
    public String name;

    /**
     *
     * @param name name of this memory address
     */
    public IRName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "NAME(" + name + ")";
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}