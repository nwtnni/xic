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

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
