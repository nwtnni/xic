package reorder;

import java.util.ArrayList;
import java.util.List;

import ir.IRLabel;
import ir.IRStmt;

public class Block {

	public String label;
	public List<IRStmt> statements;

	public Block(IRLabel label) {
		this.statements = new ArrayList<>();
		this.statements.add(label);
		this.label = label.name();
	}
	
	public void add(IRStmt statement) {
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
