package ir;

import java.util.Arrays;
import java.util.List;

/**
 * An intermediate representation for a sequence of statements
 * SEQ(s1,...,sn)
 */
public class IRSeq extends IRStmt {
    private List<IRNode> stmts;

    /**
     * @param stmts the statements
     */
    public IRSeq(IRNode... stmts) {
        this(Arrays.asList(stmts));
    }

    /**
     * Create a SEQ from a list of statements.
     * The list should not be modified subsequently.
     * @param stmts the sequence of statements
     */
    public IRSeq(List<IRNode> stmts) {
        this.stmts = stmts;
    }

    public List<IRNode> stmts() {
        return stmts;
    }

    /* Public interface for sequence. */

    public boolean add(IRNode s) {
        return stmts.add(s);
    }

    public void add(int index, IRNode s) {
        stmts.add(index, s);
    }

    public IRNode set(int index, IRNode s) {
        return stmts.set(index, s);
    }

    public IRNode get(int index) {
        return stmts.get(index);
    }

    public int size() {
        return stmts.size();
    }

    @Override
    public String label() {
        return "SEQ";
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
