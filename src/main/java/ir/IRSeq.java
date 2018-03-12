package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a sequence of statements
 * SEQ(s1,...,sn)
 */
public class IRSeq extends IRStmt {
    public List<IRNode> stmts;

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

    @Override
    public String label() {
        return "SEQ";
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     boolean modified = false;

    //     List<IRStmt> results = new ArrayList<>(stmts.size());
    //     for (IRStmt stmt : stmts) {
    //         IRStmt newStmt = (IRStmt) v.visit(this, stmt);
    //         if (newStmt != stmt) modified = true;
    //         results.add(newStmt);
    //     }

    //     if (modified) return v.nodeFactory().IRSeq(results);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     for (IRStmt stmt : stmts)
    //         result = v.bind(result, v.visit(stmt));
    //     return result;
    // }

    // @Override
    // public CheckCanonicalIRVisitor checkCanonicalEnter(
    //         CheckCanonicalIRVisitor v) {
    //     return v.enterSeq();
    // }

    // @Override
    // public boolean isCanonical(CheckCanonicalIRVisitor v) {
    //     return !v.inSeq();
    // }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
