package optimize;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import ir.*;
import optimize.graph.*;

public class IRGraphTest {
    
    IREdgeFactory<Void> ef = new IREdgeFactory<>();

	@Test
	public void testLinear() {
        IRFuncDecl fn = new IRFuncDecl("fn", "fn", 0, 0);
        fn.add(new IRMove(new IRTemp("a"), new IRConst(0)));
        fn.add(new IRMove(new IRTemp("b"), new IRConst(1)));
        fn.add(new IRMove(new IRTemp("c"), new IRConst(2)));
        fn.add(new IRReturn());

        IRCompUnit comp = new IRCompUnit("prog");
        comp.appendFunc(fn);

        IRGraphFactory<Void> gf = new IRGraphFactory<>(comp, ef);
        Map<String, IRGraph<Void>> cfgs = gf.getCfgs();

        IRFuncDecl after = cfgs.get("fn").toIR();
        assertEquals(fn.get(0), after.get(0));
        assertEquals(fn.get(1), after.get(1));
        assertEquals(fn.get(2), after.get(2));

    }
}
