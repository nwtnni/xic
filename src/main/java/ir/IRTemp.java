package ir;

/**
 * An intermediate representation for a temporary register
 * TEMP(name)
 */
public class IRTemp extends IRExpr {
    private String name;
    private boolean global;

    /**
     *
     * @param name name of this temporary register
     */
    public IRTemp(String name) {
        this.name = name;
    }

    public IRTemp(String name, boolean global) {
        this.name = name;
        this.global = global;
    }

    public String name() {
        return name;
    }

    public boolean global() {
        return global;
    }

    @Override
    public String label() {
        return "TEMP(" + name + ")";
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRTemp && name.equals(((IRTemp) obj).name);
    }
}
