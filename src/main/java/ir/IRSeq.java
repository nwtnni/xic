package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An intermediate representation for a sequence of statements
 * SEQ(s1,...,sn)
 */
public class IRSeq extends IRStmt {
    private List<IRStmt> stmts;

    /**
     * @param stmts the statements
     */
    public IRSeq(IRStmt... stmts) {
        this(new ArrayList<>(Arrays.asList(stmts)));
    }

    /**
     * Create a SEQ from a list of statements.
     * The list should not be modified subsequently.
     * @param stmts the sequence of statements
     */
    public IRSeq(List<IRStmt> stmts) {
        this.stmts = stmts;
    }

    /* Public interface for sequence. */

    public List<IRStmt> stmts() {
        return stmts;
    }

    public List<IRStmt> setStmts(List<IRStmt> s) {
        List<IRStmt> old = stmts;
        stmts = s;
        return old;
    }

    public boolean add(IRStmt s) {
        return stmts.add(s);
    }

    public void add(int index, IRStmt s) {
        stmts.add(index, s);
    }

    public IRStmt set(int index, IRStmt s) {
        return stmts.set(index, s);
    }

    public IRStmt remove(int index) {
        return stmts.remove(index);
    }

    public IRStmt get(int index) {
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
