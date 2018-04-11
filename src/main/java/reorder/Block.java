package reorder;

import java.util.ArrayList;
import java.util.List;

import ir.IRLabel;
import ir.IRNode;

public class Block {

	public String label;
	public List<IRNode> statements;

	public Block(IRLabel label) {
		this.statements = new ArrayList<>();
		this.statements.add(label);
		this.label = label.name;
	}
	
	public void add(IRNode statement) {
		statements.add(statement);
	}
	
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
