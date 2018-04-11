package reorder;

import java.util.ArrayList;
import java.util.List;

import ir.IRNode;
import util.Graph;

public class ControlFlow {

	private ControlFlow() {
		graph = new Graph<>();
	}
	
	private Graph<Block, Void> graph;
	
	private enum State {
		
	}
	
	public static ControlFlow from(List<IRNode> program) {
		return null;
	}
}
