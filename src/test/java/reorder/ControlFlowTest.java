package reorder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import ir.*;

public class ControlFlowTest {
	
	@Test
	public void testLinear() {
		List<IRStmt> program = new ArrayList<>();
		program.add(new IRMove(new IRMem(new IRTemp("test1")), new IRConst(1)));
		program.add(new IRMove(new IRMem(new IRTemp("test2")), new IRConst(2)));
		program.add(new IRMove(new IRMem(new IRTemp("test3")), new IRConst(3)));
		program.add(new IRMove(new IRMem(new IRTemp("test4")), new IRConst(4)));
		program.add(new IRMove(new IRMem(new IRTemp("test5")), new IRConst(5)));
		
		ControlFlow cfg = ControlFlow.from(program);
		assertEquals(cfg.size(), 2);
		assertEquals(cfg.neighbors(cfg.start()).size(), 1);
	}
	
	@Test
	public void testJump() {
		List<IRStmt> program = new ArrayList<>();
		
		IRLabel exit = new IRLabel("test");
		program.add(new IRJump(exit));
		program.add(exit);
		
		ControlFlow cfg = ControlFlow.from(program);
		assertEquals(cfg.size(), 3);
		
		Block start = cfg.start();
		assertEquals(cfg.neighbors(start).size(), 1);
		Block next = cfg.neighbors(start).get(0);
		assertEquals(cfg.neighbors(next).size(), 1);
	}
	
	@Test
	public void testConditional() {
		List<IRStmt> program = new ArrayList<>();
		IRLabel t = new IRLabel("true");
		IRLabel f = new IRLabel("false");
		IRLabel e = new IRLabel("exit");

		program.add(new IRCJump(new IRConst(1), t, f));
		program.add(t);
		program.add(new IRMove(new IRMem(new IRTemp("test1")), new IRConst(1)));
		program.add(new IRJump(e));
		program.add(f);
		program.add(new IRMove(new IRMem(new IRTemp("test2")), new IRConst(2)));
		program.add(e);
		
		ControlFlow cfg = ControlFlow.from(program);
		assertEquals(cfg.size(), 5);
		
		Block start = cfg.start();
		assertEquals(cfg.neighbors(start).size(), 2);
		Block tb = cfg.neighbors(start).get(0);
		Block fb = cfg.neighbors(start).get(1);
		assertEquals(tb.label, "true");
		assertEquals(fb.label, "false");
		
		assertEquals(cfg.neighbors(tb).size(), 1);
		assertEquals(cfg.neighbors(tb).get(0).label, "exit");
		
		assertEquals(cfg.neighbors(fb).size(), 1);
		assertEquals(cfg.neighbors(fb).get(0).label, "exit");
	}
	
	@Test
	public void testReturn() {
		List<IRStmt> program = new ArrayList<>();
		IRLabel t = new IRLabel("true");
		IRLabel f = new IRLabel("false");
		IRLabel e = new IRLabel("exit");

		program.add(new IRCJump(new IRConst(1), t, f));
		program.add(t);
		program.add(new IRMove(new IRMem(new IRTemp("test1")), new IRConst(1)));
		program.add(new IRJump(e));
		program.add(f);
		program.add(new IRReturn());
		program.add(e);

		ControlFlow cfg = ControlFlow.from(program);
		assertEquals(cfg.size(), 5);
		
		Block start = cfg.start();
		assertEquals(cfg.neighbors(start).size(), 2);
		Block tb = cfg.neighbors(start).get(0);
		Block fb = cfg.neighbors(start).get(1);
		assertEquals(tb.label, "true");
		assertEquals(fb.label, "false");
		
		assertEquals(cfg.neighbors(tb).size(), 1);
		assertEquals(cfg.neighbors(tb).get(0).label, "exit");
		
		assertEquals(cfg.neighbors(fb).size(), 0);
	}
}
