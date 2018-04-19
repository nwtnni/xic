package graph;

import org.junit.Test;

import util.Graph;

import static org.junit.Assert.assertEquals;

public class GraphTest {
	
	class DummyGraph extends Graph<Integer, Integer> {}
 	
	@Test
	public void testInsert() {
		DummyGraph g = new DummyGraph();
		g.put(1, 2, 3);
		assertEquals(g.get(1, 2), new Integer(3));
	}
	
	@Test
	public void testRemove() {
		DummyGraph g = new DummyGraph();
		g.put(1, 2, 3);
		g.remove(1, 2);
		assertEquals(g.size(), 0);
	}
	
	@Test
	public void testUnique() {
		DummyGraph g = new DummyGraph();
		g.put(1, 2, 3);
		g.put(1, 2, 4);
		assertEquals(g.size(), 1);
		assertEquals(g.get(1, 2), new Integer(4));
	}
	
	@Test
	public void testDirected() {
		DummyGraph g = new DummyGraph();
		g.put(1, 2, 3);
		g.put(2, 1, 4);
		assertEquals(g.size(), 2);
		assertEquals(g.get(1, 2), new Integer(3));
		assertEquals(g.get(2, 1), new Integer(4));
	}
}
