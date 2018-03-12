package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a function call
 * CALL(e_target, e_1, ..., e_n)
 */
public class IRCall extends IRExpr {
    protected IRNode target;
    protected List<IRNode> args;

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRNode target, IRNode... args) {
        this(target, Arrays.asList(args));
    }

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRNode target, List<IRNode> args) {
        this.target = target;
        this.args = args;
    }

    public IRNode target() {
        return target;
    }

    public List<IRNode> args() {
        return args;
    }

    @Override
    public String label() {
        return "CALL";
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     boolean modified = false;

    //     IRExpr target = (IRExpr) v.visit(this, this.target);
    //     if (target != this.target) modified = true;

    //     List<IRExpr> results = new ArrayList<>(args.size());
    //     for (IRExpr arg : args) {
    //         IRExpr newExpr = (IRExpr) v.visit(this, arg);
    //         if (newExpr != arg) modified = true;
    //         results.add(newExpr);
    //     }

    //     if (modified) return v.nodeFactory().IRCall(target, results);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(target));
    //     for (IRExpr arg : args)
    //         result = v.bind(result, v.visit(arg));
    //     return result;
    // }

    // @Override
    // public boolean isCanonical(CheckCanonicalIRVisitor v) {
    //     return !v.inExpr();
    // }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CALL");
        target.printSExp(p);
        for (IRNode arg : args)
            arg.printSExp(p);
        p.endList();
    }
}
