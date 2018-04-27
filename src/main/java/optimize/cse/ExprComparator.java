package optimize.cse;

import java.util.Iterator;
import java.util.Set;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicInternalException;
public class ExprComparator implements Comparator<IRExpr> {
    public int sizeExpr(IRExpr e) {
        if (e instanceof IRTemp || e instanceof IRConst || e instanceof IRName) {
            return 1;
        } 
        if (e instanceof IRBinOp) {
            IRBinOp b = (IRBinOp) e;
            return sizeExpr(b.right) + sizeExpr(b.left) + 1;
        }
        if (e instanceof IRMem) {
            return sizeExpr(((IRMem) e).expr) + 1;
        }
        return 0;
    }

    public int compare(IRExpr e1, IRExpr e2) {
      return sizeExpr(e1) - sizeExpr(e2);
    }

}