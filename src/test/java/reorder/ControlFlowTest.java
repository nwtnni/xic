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
		
		program.add(new IRJump(new IRName("test")));
		program.add(new IRLabel("test"));
		
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
		program.add(new IRCJump(new IRConst(1), "true", "false"));
		program.add(new IRLabel("true"));
		program.add(new IRMove(new IRMem(new IRTemp("test1")), new IRConst(1)));
		program.add(new IRJump(new IRName("exit")));
		program.add(new IRLabel("false"));
		program.add(new IRMove(new IRMem(new IRTemp("test2")), new IRConst(2)));
		program.add(new IRLabel("exit"));
		
		ControlFlow cfg = ControlFlow.from(program);
		assertEquals(cfg.size(), 5);
		
		Block start = cfg.start();
		assertEquals(cfg.neighbors(start).size(), 2);
		Block t = cfg.neighbors(start).get(0);
		Block f = cfg.neighbors(start).get(1);
		assertEquals(t.label, "true");
		assertEquals(f.label, "false");
		
		assertEquals(cfg.neighbors(t).size(), 1);
		assertEquals(cfg.neighbors(t).get(0).label, "exit");
		
		assertEquals(cfg.neighbors(f).size(), 1);
		assertEquals(cfg.neighbors(f).get(0).label, "exit");
	}
	
	@Test
	public void testReturn() {
		List<IRStmt> program = new ArrayList<>();
		program.add(new IRCJump(new IRConst(1), "true", "false"));
		program.add(new IRLabel("true"));
		program.add(new IRMove(new IRMem(new IRTemp("test1")), new IRConst(1)));
		program.add(new IRJump(new IRName("exit")));
		program.add(new IRLabel("false"));
		program.add(new IRReturn());
		program.add(new IRLabel("exit"));

		ControlFlow cfg = ControlFlow.from(program);
		assertEquals(cfg.size(), 5);
		
		Block start = cfg.start();
		assertEquals(cfg.neighbors(start).size(), 2);
		Block t = cfg.neighbors(start).get(0);
		Block f = cfg.neighbors(start).get(1);
		assertEquals(t.label, "true");
		assertEquals(f.label, "false");
		
		assertEquals(cfg.neighbors(t).size(), 1);
		assertEquals(cfg.neighbors(t).get(0).label, "exit");
		
		assertEquals(cfg.neighbors(f).size(), 0);
	}
}
