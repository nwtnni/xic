package emit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import ir.*;

public class TracerTest {
	
	@Test
	public void testVisitReturn() {
		IRReturn r = new IRReturn();
		Tracer t = new Tracer();
		Tracer.Flow f = r.accept(t);
		assertEquals(f.t, null);
		assertEquals(f.f, null);
		assertEquals(f.kind, Tracer.Flow.Kind.RETURN);
	}
	
	@Test
	public void testVisitJump() {
		IRJump j = new IRJump(new IRName("test"));
		Tracer t = new Tracer();
		Tracer.Flow f = j.accept(t);
		assertEquals(f.t, "test");
		assertEquals(f.f, null);
		assertEquals(f.kind, Tracer.Flow.Kind.JUMP);
	}
	
	@Test
	public void testVisitCJump() {
		IRCJump j = new IRCJump(new IRConst(1), "true", "false");
		Tracer t = new Tracer();
		Tracer.Flow f = j.accept(t);
		assertEquals(f.t, "true");
		assertEquals(f.f, "false");
		assertEquals(f.kind, Tracer.Flow.Kind.CJUMP);
	}
	
	@Test
	public void testVisitLabel() {
		IRLabel l = new IRLabel("test");
		Tracer t = new Tracer();
		Tracer.Flow f = l.accept(t);
		assertEquals(f.t, "test");
		assertEquals(f.f, null);
		assertEquals(f.kind, Tracer.Flow.Kind.LABEL);
	}
	
	@Test
	public void testBlock() {
		
		
	}
}
