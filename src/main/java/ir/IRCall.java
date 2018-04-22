package ir;

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
    private List<IRNode> args;

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRName target, IRNode... args) {
        this(target, Arrays.asList(args));
    }

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRName target, List<IRNode> args) {
        this.target = target;
        this.args = args;
    }

    /* Public interface for call. */

    public IRName target() {
        return target;
    }

    public List<IRNode> args() {
        return args;
    }

    public List<IRNode> setArgs(List<IRNode> args) {
        List<IRNode> old = this.args;
        this.args = args;
        return old;
    }

    public boolean add(IRNode s) {
        return args.add(s);
    }

    public void add(int index, IRNode s) {
        args.add(index, s);
    }

    public IRNode set(int index, IRNode s) {
        return args.set(index, s);
    }

    public IRNode get(int index) {
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
