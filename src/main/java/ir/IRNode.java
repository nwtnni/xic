package ir;

import java.util.UUID;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public abstract class IRNode {
    public IRNode() {
        this.id = UUID.randomUUID();
    }

    public abstract <T> T accept(IRVisitor<T> v);

    public abstract String label();

    public UUID id;

    @Override
    public String toString() {
        return Printer.toString(this);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRNode && id.equals(((IRNode) obj).id);
    }
}
