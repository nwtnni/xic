package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** RETURN statement */
public class IRReturn extends IRStmt {
    private List<IRExpr> rets;

    /**
     * @param rets values to return
     */
    public IRReturn(IRExpr... rets) {
        this(new ArrayList<>(Arrays.asList(rets)));
    }

    /**
     * @param rets values to return
     */
    public IRReturn(List<IRExpr> rets) {
        this.rets = rets;
    }

    public List<IRExpr> rets() {
        return rets;
    }

    public List<IRExpr> setRets(List<IRExpr> rets) {
        List<IRExpr> old = this.rets;
        this.rets = rets;
        return old;
    }

    public boolean add(IRExpr s) {
        return rets.add(s);
    }

    public void add(int index, IRExpr s) {
        rets.add(index, s);
    }

    public IRExpr set(int index, IRExpr s) {
        return rets.set(index, s);
    }

    public IRExpr get(int index) {
        return rets.get(index);
    }

    public int size() {
        return rets.size();
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
