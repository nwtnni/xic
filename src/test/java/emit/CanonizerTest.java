package emit;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import ir.*;

public class CanonizerTest {

	@Test
	public void testIRTemp() {
		IRTemp temp = new IRTemp("test");
		IRNode result = Canonizer.canonize(temp);
		List<IRStmt> statements = Canonizer.debug(temp);
		assertEquals(true, result instanceof IRTemp);
		assertEquals(0, statements.size());
	}

	@Test
	public void testIRName() {
		IRName name = new IRName("test");;
		IRNode result = Canonizer.canonize(name);
		List<IRStmt> statements = Canonizer.debug(name);
		assertEquals(true, result instanceof IRName);
		assertEquals(0, statements.size());
	}
	
	@Test
	public void testIRConst() {
		IRConst c = new IRConst(5);
		IRNode result = Canonizer.canonize(c);
		List<IRStmt> statements = Canonizer.debug(c);
		assertEquals(true, result instanceof IRConst);
		assertEquals(0, statements.size());
	}
	
	@Test
	public void testIRLabel() {
		IRLabel l = new IRLabel("test");
		IRNode result = Canonizer.canonize(l);
		List<IRStmt> statements = Canonizer.debug(l);
		
		// IRLabel is a statement
		assertEquals(null, result);
		assertEquals(1, statements.size());
	}
	
	@Test
	public void testIRBinOp() {
		// Should not hoist expression with constants
		IRConst c1 = new IRConst(5);
		IRConst c2 = new IRConst(6);
		IRBinOp b = new IRBinOp(IRBinOp.OpType.ADD, c1, c2);
		
		IRNode result = Canonizer.canonize(b);
		List<IRStmt> statements = Canonizer.debug(b);
		
		assertEquals(true, result instanceof IRBinOp);
		assertEquals(0, statements.size());

		// Should hoist expression with potential side effect
		IRCall f1 = new IRCall(new IRName("foo"));
		b.left = f1;
		result = Canonizer.canonize(b);
		statements = Canonizer.debug(b);

		assertEquals(true, statements.get(0) instanceof IRMove);

		IRMove move = (IRMove) statements.get(0);
		assertEquals(true, move.target() instanceof IRTemp);
		assertEquals(true, move.src() instanceof IRCall);
		
		IRBinOp bop = (IRBinOp) result;
		assertEquals(true, bop.left() instanceof IRTemp);
		assertEquals(true, bop.right() instanceof IRConst);
	}
	
	@Test
	public void testIRCall() {
		IRTemp temp = IRTempFactory.generate();
		IRConst val = new IRConst(6);
		IRESeq a1 = new IRESeq(new IRMove(temp, val), temp);
		IRConst a2 = new IRConst(5);
		
		IRCall c = new IRCall(new IRName("test"), a1, a2);
		
		IRNode result = Canonizer.canonize(c);
		List<IRStmt> statements = Canonizer.debug(c);

		// Moved IRCall result into temp
		assertEquals(true, result instanceof IRTemp);
		
		assertEquals(4, statements.size());
		
		// Hoisted IRESeq
		IRStmt s1 = statements.get(0);
		assertEquals(true, s1 instanceof IRMove);
		
		// Move lowered IRESeq into temp
		IRStmt s2 = statements.get(1);
		assertEquals(true, s2 instanceof IRMove);
		
		// Move IRConst into temp
		IRStmt s3 = statements.get(2);
		assertEquals(true, s3 instanceof IRMove);
		
		// Move IRCall into temp
		IRStmt s4 = statements.get(3);
		assertEquals(true, s4 instanceof IRMove);
		
		IRMove move = (IRMove) s4;
		assertEquals(true, move.target() instanceof IRTemp);
		assertEquals(true, move.src() instanceof IRCall);
	}
	
	@Test
	public void testIRCJump() {
		IRTemp temp = IRTempFactory.generate();
		IRConst cond = new IRConst(1);
		IRESeq c = new IRESeq(new IRMove(temp, cond), temp);
		IRCJump j = new IRCJump(c, new IRLabel("true"));
		IRNode result = Canonizer.canonize(j);
		List<IRStmt> statements = Canonizer.debug(j);
		
		// IRCJump is a statement
		assertEquals(null, result);
		assertEquals(2, statements.size());
		
		// Hoisted IRESeq
		IRStmt s1 = statements.get(0);
		assertEquals(true, s1 instanceof IRMove);
		
		// IRCJump with lowered IRESeq
		IRStmt s2 = statements.get(1);
		assertEquals(true, s2 instanceof IRCJump);
		
		IRCJump jump = (IRCJump) s2;
		assertEquals(true, jump.cond instanceof IRTemp);
		assertEquals(true, jump.trueName().equals("true"));
	}
	
	@Test
	public void testIRJump() {
		IRTemp temp = IRTempFactory.generate();
		IRConst c = new IRConst(1);
		IRESeq e = new IRESeq(new IRMove(temp, c), temp);
		IRJump j = new IRJump(e);
		
		IRNode result = Canonizer.canonize(j);
		List<IRStmt> statements = Canonizer.debug(j);
		
		// IRJump is a statement
		assertEquals(null, result);
		assertEquals(2, statements.size());
		
		// Hoisted IRESeq
		IRStmt s1 = statements.get(0);
		assertEquals(true, s1 instanceof IRMove);
		
		IRStmt s2 = statements.get(1);
		assertEquals(true, s2 instanceof IRJump);
		
		IRJump jump = (IRJump) s2;
		assertEquals(true, jump.target() instanceof IRTemp);
	}
	
	@Test
	public void testIRESeq() {
		IRTemp t1 = IRTempFactory.generate();
		IRTemp t2 = IRTempFactory.generate();
		IRConst c = new IRConst(0);
		IRESeq e1 = new IRESeq(new IRMove(t1, c), t1);
		IRESeq e2 = new IRESeq(new IRMove(t2, e1), t2);

		IRNode result = Canonizer.canonize(e2);
		List<IRStmt> statements = Canonizer.debug(e2);
		
		assertEquals(true, result instanceof IRTemp);
		assertEquals(2, statements.size());
		
		// Hoisted e1
		IRStmt s1 = statements.get(0);
		assertEquals(true, s1 instanceof IRMove);
		IRMove m1 = (IRMove) s1;
		assertEquals(true, m1.target() == t1);
		assertEquals(true, m1.src() == c);
		
		// Hoisted e2
		IRStmt s2 = statements.get(1);
		assertEquals(true, s2 instanceof IRMove);
		IRMove m2 = (IRMove) s2;
		assertEquals(true, m2.target() == t2);
		assertEquals(true, m2.src() == t1);
	}

	@Test
	public void testIRExp() {
		IRConst a1 = new IRConst(6);
		IRConst a2 = new IRConst(5);
		IRCall c = new IRCall(new IRName("test"), a1, a2);
		IRExp e = new IRExp(c);
		
		IRNode result = Canonizer.canonize(e);
		List<IRStmt> statements = Canonizer.debug(e);

		// IRExp discards result
		assertEquals(result, null);
		
		assertEquals(1, statements.size());
		
		// // Move IRConst into temp
		// IRStmt s1 = statements.get(0);
		// assertEquals(true, s1 instanceof IRMove);

		// // Move IRConst into temp
		// IRStmt s2 = statements.get(1);
		// assertEquals(true, s2 instanceof IRMove);
		
		// // Move result of IRCall into temp
		// IRStmt s3 = statements.get(2);
		// assertEquals(true, s3 instanceof IRMove);
	}
	
	@Test
	public void testIRMoveTemp() {
		IRConst c = new IRConst(0);
		IRTemp t = IRTempFactory.generate();
		IRMove m = new IRMove(t, c);
		
		IRNode result = Canonizer.canonize(m);
		List<IRStmt> statements = Canonizer.debug(m);
		
		// IRMove is a statement
		assertEquals(null, result);
		assertEquals(1, statements.size());
		
		// Move src into target
		IRStmt s1 = statements.get(0);
		assertEquals(true, s1 instanceof IRMove);
		
		IRMove move = (IRMove) s1;
		assertEquals(true, move.target() == t);
		assertEquals(true, move.src() == c);
	}
	
	@Test
	public void testIRMoveMem() {
		IRTemp t = IRTempFactory.generate();
		IRConst c = new IRConst(0);
		IRMem mem = new IRMem(new IRESeq(new IRMove(t, c), t));
		IRMove m = new IRMove(mem, t);

		IRNode result = Canonizer.canonize(m);
		List<IRStmt> statements = Canonizer.debug(m);
		
		// IRMove is a statement
		assertEquals(null, result);
		assertEquals(3, statements.size());
		
		// Hoist IRESeq inside IRMem expr
		IRStmt s1 = statements.get(0);
		assertEquals(true, s1 instanceof IRMove);
		IRMove m1 = (IRMove) s1;
		assertEquals(true, m1.target() == t);
		assertEquals(true, m1.src() == c);
		
		// Hoist IRMem expr and store in unique temp
		IRStmt s2 = statements.get(1);
		assertEquals(true, s2 instanceof IRMove);
		IRMove m2 = (IRMove) s2;
		assertEquals(true, m2.target() instanceof IRTemp);
		
		// Unique!
		assertEquals(true, m2.target() != t);
		assertEquals(true, m2.src() == t);
		
		// Hoist move
		IRStmt s3 = statements.get(2);
		assertEquals(true, s3 instanceof IRMove);
		IRMove m3 = (IRMove) s3;
		assertEquals(true, m3.target() instanceof IRMem);
		assertEquals(true, m3.src() == t);
		
		// Unique!		
		IRMem m4 = (IRMem) m3.target();
		assertEquals(true, m4.expr() != t);
	}
	
	@Test
	public void testIRReturn() {
		IRConst a1 = new IRConst(2);
		IRTemp temp = IRTempFactory.generate();
		IRConst val = new IRConst(6);
		IRESeq a2 = new IRESeq(new IRMove(temp, val), temp);
		IRReturn r = new IRReturn(a1, a2);
		
		IRNode result = Canonizer.canonize(r);
		List<IRStmt> statements = Canonizer.debug(r);
		
		// IRReturn is a statement
		assertEquals(null, result);
		assertEquals(4, statements.size());
		
		// Store IRConst in unique IRTemp
		IRStmt s1 = statements.get(0);
		assertEquals(true, s1 instanceof IRMove);
		IRMove m1 = (IRMove) s1;
		assertEquals(true, m1.target() != temp);
		assertEquals(true, m1.src() == a1);
		
		// Hoist IRESeq inside a2
		IRStmt s2 = statements.get(1);
		assertEquals(true, s2 instanceof IRMove);
		IRMove m2 = (IRMove) s2;
		assertEquals(true, m2.target() == temp);
		assertEquals(true, m2.src() == val);
		
		// Store IRESeq result in unique IRTemp
		IRStmt s3 = statements.get(2);
		assertEquals(true, s3 instanceof IRMove);
		IRMove m3 = (IRMove) s3;
		
		assertEquals(true, 
			!( m3.target() == temp 
			|| m3.target() == m2.target()
			|| m3.target() == m1.target()
			)
		);
		
		// Final IRReturn
		IRStmt s4 = statements.get(3);
		assertEquals(true, s4 instanceof IRReturn);
		IRReturn ret = (IRReturn) s4;
		
		// Should both be IRTemps corresponding to previous IRMoves
		assertEquals(2, ret.size());
		
		IRNode e1 = ret.get(0);
		assertEquals(true, e1 == m1.target());
		IRNode e2 = ret.get(1);
		assertEquals(true, e2 == m3.target());
	}
}
