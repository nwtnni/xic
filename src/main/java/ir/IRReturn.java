package ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** RETURN statement */
public class IRReturn extends IRStmt {
    protected List<IRNode> rets;

    /**
     * @param rets values to return
     */
    public IRReturn(IRNode... rets) {
        this(Arrays.asList(rets));
    }

    /**
     * @param rets values to return
     */
    public IRReturn(List<IRNode> rets) {
        this.rets = rets;
    }

    public List<IRNode> rets() {
        return rets;
    }

    @Override
    public String label() {
        return "RETURN";
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     boolean modified = false;

    //     List<IRExpr> results = new ArrayList<>(rets.size());

    //     for (IRExpr ret : rets) {
    //         IRExpr newExpr = (IRExpr) v.visit(this, ret);
    //         if (newExpr != ret) modified = true;
    //         results.add(newExpr);
    //     }

    //     if (modified) return v.nodeFactory().IRReturn(results);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     for (IRExpr ret : rets)
    //         result = v.bind(result, v.visit(ret));
    //     return result;
    // }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
