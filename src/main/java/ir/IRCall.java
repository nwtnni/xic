package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An intermediate representation for a function call
 * CALL(e_target, e_1, ..., e_n)
 */
public class IRCall extends IRExpr {
    
    private IRExpr target;

    private List<IRExpr> args;

    private int numRets;

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, int rets, IRExpr... args) {
        this(target, rets, new ArrayList<>(Arrays.asList(args)));
    }

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, int rets, List<IRExpr> args) {
        this.target = target;
        this.args = args;
        this.numRets = rets;
    }

    /* Public interface for call. */

    public IRExpr target() {
        return target;
    }

    public List<IRExpr> args() {
        return args;
    }

    public List<IRExpr> setArgs(List<IRExpr> args) {
        List<IRExpr> old = this.args;
        this.args = args;
        return old;
    }

    public boolean add(IRExpr s) {
        return args.add(s);
    }

    public void add(int index, IRExpr s) {
        args.add(index, s);
    }

    public IRExpr set(int index, IRExpr s) {
        return args.set(index, s);
    }

    public IRExpr get(int index) {
        return args.get(index);
    }

    public int numArgs() {
        return args.size();
    }

    public int numRets() {
        return numRets;
    }

    @Override
    public String label() {
        return "CALL";
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }

    // Calls are never in the set of expressions for CSE, don't need equals method
}
