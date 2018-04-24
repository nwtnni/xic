package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An intermediate representation for a function call
 * CALL(e_target, e_1, ..., e_n)
 */
public class IRCall extends IRExpr {
    /**
    *
    * target and args were IRExpr in release code
    */
    private IRName target;
    private List<IRExpr> args;

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRName target, IRExpr... args) {
        this(target, new ArrayList<>(Arrays.asList(args)));
    }

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRName target, List<IRExpr> args) {
        this.target = target;
        this.args = args;
    }

    /* Public interface for call. */

    public IRName target() {
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

    public int size() {
        return args.size();
    }

    @Override
    public String label() {
        return "CALL";
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
