package ir;

import java.util.UUID;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public abstract class IRNode {
    public IRNode() {
        this.id = UUID.randomUUID();
        this.CSEin = null;
        this.kill = new HashSet<>();
        this.exprs = new HashSet<IRExpr>();
        this.delMem = false;
    }

    public abstract <T> T accept(IRVisitor<T> v);

    public abstract String label();

    public UUID id;

    public Map<IRExpr, IRStmt> CSEin;

    public Set<IRExpr> kill;

    public Set<IRExpr> exprs;

    public boolean delMem; 

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
