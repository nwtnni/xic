package ir;

/**
 * An intermediate representation for naming a memory address
 */
public class IRLabel extends IRStmt {
    private String name;

    /**
     *
     * @param name name of this memory address
     */
    public IRLabel(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "LABEL(" + name + ")";
    }
    
    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
