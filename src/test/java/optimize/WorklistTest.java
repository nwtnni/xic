package optimize;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.List;

import org.junit.Test;

import ir.*;
import optimize.*;
import util.PairEdge;

public class WorklistTest {
    
    IREdgeFactory<Set<IRExpr>> ef = new IREdgeFactory<>();

    @Test
    public void testBasic() {

        /*
         * a = 2 
         * b = a + 2
         * c = a + 2
         */
        IRFuncDecl fn = new IRFuncDecl("fn");
        IRTemp a = new IRTemp("a");
        IRMove mA = new IRMove(a, new IRConst(2));
        IRBinOp bo1 = new IRBinOp(IRBinOp.OpType.ADD, a, new IRConst(2));
        IRTemp b = new IRTemp("b");
        IRTemp c = new IRTemp("c");
        IRMove mB = new IRMove(b, bo1);

        IRBinOp bo2 = new IRBinOp(IRBinOp.OpType.ADD, a, new IRConst(2));
        IRMove mC = new IRMove(c, bo2);
        IRSeq s = new IRSeq(mA, mB, mC, new IRReturn());
        fn.add(s);

        IRCompUnit comp = new IRCompUnit("prog");
        comp.appendFunc(fn);
//
        IRGraphFactory<Set<IRExpr>> gf = new IRGraphFactory<Set<IRExpr>>(comp, ef);
        List<IRGraph<Set<IRExpr>>> cfgs = gf.getCfgs();
//
        IRGraph<Set<IRExpr>> first = cfgs.get(0);
        WorklistVisitor.annotateNodes(first.start);
        
//        for (IRExpr e : mB.exprs) {
//        		if (e instanceof IRBinOp) {
//        			System.out.println(((IRBinOp) e).type());
//        		}
//        		if (e instanceof IRTemp) {
//        			System.out.println(((IRTemp) e).name());
//        		}
//        		System.out.println(e.toString());
//        }
        CSEWorklist cse = new CSEWorklist();
        cse.annotate(first);
        assertEquals(mA.kill.contains(a), true);
        assertEquals(s.exprs.contains(bo1), true);
        assertEquals(mB.exprs.contains(bo1), true);
        assertEquals(mB.exprs.size(), 3);
        assertEquals(bo1.exprs.contains(a), true);

    }
}
