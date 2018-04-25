package ir;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public abstract class IRNode {
    public IRNode() {
        this.id = UUID.randomUUID();
        this.CSEin = new HashSet<>();
        this.kill = new HashSet<>();
        this.exprs = new HashSet<>();
        this.hasMem = false;
    }

    public abstract <T> T accept(IRVisitor<T> v);

    public abstract String label();

    public UUID id;

    public Set<IRExpr> CSEin;

    public Set<IRExpr> kill;

    public Set<IRExpr> exprs;

    public boolean hasMem; 

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
