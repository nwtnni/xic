package optimize.cse;

import java.util.Comparator;

import ir.*;

/** 
 * Comparator used to sort expressions in the priority queue used during
 * CSE to greedily replace the largest expressions.
 */
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

    // This comparator sorts in descending order, flip to change to ascending
    public int compare(IRExpr e1, IRExpr e2) {
      return sizeExpr(e2) - sizeExpr(e1);
    }

}