package graph;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.List;

public class GraphTest {

	class DummyNode implements Node {
		
		private int id;
		
		public DummyNode(int id) {
			this.id = id;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof DummyNode)) return false;
			DummyNode b = (DummyNode) o;
			return id == b.id;
		}
		
		@Override
		public int hashCode() {
			return id;
		}
	}
	
	class DummyGraph extends Graph<DummyNode, Integer> {}
 	
	@Test
	public void testInsert() {
		DummyGraph g = new DummyGraph();
		DummyNode a = new DummyNode(1);
		DummyNode b = new DummyNode(2);
		g.insert(a, b, 3);
		assertEquals(g.get(a, b), new Integer(3));
	}
	
	@Test
	public void testRemove() {
		DummyGraph g = new DummyGraph();
		DummyNode a = new DummyNode(1);
		DummyNode b = new DummyNode(2);
		g.insert(a, b, 3);
		g.remove(a, b);
		assertEquals(g.size(), 0);
	}
	
	@Test
	public void testUnique() {
		DummyGraph g = new DummyGraph();
		DummyNode a = new DummyNode(1);
		DummyNode b = new DummyNode(2);
		g.insert(a, b, 3);
		g.insert(a, b, 4);
		assertEquals(g.size(), 1);
		assertEquals(g.get(a, b), new Integer(4));
	}
	
	@Test
	public void testDirected() {
		DummyGraph g = new DummyGraph();
		DummyNode a = new DummyNode(1);
		DummyNode b = new DummyNode(2);
		g.insert(a, b, 3);
		g.insert(b, a, 4);
		assertEquals(g.size(), 2);
		assertEquals(g.get(a, b), new Integer(3));
		assertEquals(g.get(b, a), new Integer(4));
	}
}
