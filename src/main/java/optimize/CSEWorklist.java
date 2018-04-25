package optimize;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicInternalException;
public class CSEWorklist {

    public boolean kill(Set<IRExpr> killSet, IRExpr e) {
        if (e instanceof IRTemp && killSet.contains(e)) {
            return true;
        }
        if (e instanceof IRBinOp) {
            IRBinOp b = (IRBinOp) e;
            return kill(killSet, b.left) || kill(killSet, b.right);
        }
        return false;
    }

    public Set<IRExpr> transfer(Set<IRExpr> inSet, IRStmt s) {
        Set<IRExpr> out = new HashSet<IRExpr>(inSet);
        Set<IRExpr> tempOut = new HashSet<IRExpr>(inSet);
        out.addAll(s.exprs);
        for (IRExpr e : tempOut) {
            if (e.hasMem || kill(s.kill, e)) {
                out.remove(e);
            }
        }

        return out;
    }

    public void meet(IRGraph<Set<IRExpr>> g, IRStmt v) {
        Set<PairEdge<IRStmt, Set<IRExpr>>> allOut = g.incomingEdgesOf(v);
        Iterator<PairEdge<IRStmt, Set<IRExpr>>> iter = allOut.iterator();
        Set<IRExpr> in = new HashSet<IRExpr>();

        if (iter.hasNext()) {
            in = iter.next().value;
        }

        while (iter.hasNext()) {
            Set<IRExpr> s = iter.next().value;
            in.retainAll(s);
        }

        v.CSEin = in;

    }

    public void annotate(IRGraph<Set<IRExpr>> g) {
        Set<IRStmt> vertices = g.vertexSet();
        
        Queue<IRStmt> w = new LinkedList<IRStmt>();
        Set<IRExpr> allExprs = new HashSet<IRExpr>();
        for (IRStmt v : vertices) {
            w.add(v);
            allExprs.addAll(v.exprs);
        }
        for (IRStmt v : vertices) {
            for (PairEdge<IRStmt, Set<IRExpr>> e : g.incomingEdgesOf(v)) {
                e.value = new HashSet<IRExpr>(allExprs);
            }
        }
        while (!w.isEmpty()) {
            IRStmt v = w.remove();
            meet(g, v);
            Set<IRExpr> out = transfer(v.CSEin, v);
            if (!(v.CSEin).equals(out)) {
                for (PairEdge<IRStmt, Set<IRExpr>> e : g.incomingEdgesOf(v)) {
                    w.add(e.head);
                }
            }
        }
    }
}




