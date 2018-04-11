package reorder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import ir.*;
import reorder.Tracer;

public class ControlFlowTest {
	
	@Test
	public void testBasic() {
		List<IRNode> program = new ArrayList<>();
		
		program.add(new IRJump(new IRName("test")));
		program.add(new IRLabel("test"));
		
		ControlFlow cfg = ControlFlow.from(program);
		assertEquals(cfg.size(), 2);
		assertEquals(cfg.neighbors(cfg.start()).size(), 1);
	}
}
