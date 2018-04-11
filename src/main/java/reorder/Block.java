package reorder;

import java.util.List;

import ir.IRNode;
import util.Node;

public class Block implements Node {

	public String label;
	public List<IRNode> statements;
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Block)) return false;
		
		Block b = (Block) o;
		return label.equals(b.label);
	}
	
	@Override
	public int hashCode() {
		return label.hashCode();
	}
}
