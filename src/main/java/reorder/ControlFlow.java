package reorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import emit.IRLabelFactory;
import ir.IRCJump;
import ir.IRJump;
import ir.IRLabel;
import ir.IRName;
import ir.IRNode;
import ir.IRReturn;
import util.Graph;

public class ControlFlow {

	private ControlFlow() {
		graph = new Graph<>();
		blocks = new HashMap<>();
	}
	
	private Block start;
	private Graph<String, Void> graph;
	private Map<String, Block> blocks;
	
	private enum State {
		WITHIN_BLOCK,
		AFTER_JUMP,
	}
	
	public static ControlFlow from(List<IRNode> program) {
		
		List<IRNode> statements = new ArrayList<>(program);
		IRLabel end = IRLabelFactory.generate("_END");
		statements.add(new IRJump(new IRName(end.name)));
		statements.add(end);
		
		ControlFlow cfg = new ControlFlow();
		State state = State.WITHIN_BLOCK;
		Block block = new Block(IRLabelFactory.generate("_START"));
		cfg.start = block;
		
		for (IRNode s : statements) {
			
			if (s instanceof IRLabel) {
				IRLabel label = (IRLabel) s;
				switch (state) {
				case WITHIN_BLOCK:
					block.add(new IRJump(new IRName(label.name)));
					cfg.blocks.put(block.label, block);
					cfg.graph.put(block.label, label.name, null);
					break;
				case AFTER_JUMP:
					block = new Block(label);
					state = State.WITHIN_BLOCK;
					break;
				}
				continue;
			} 
			
			// Should never happen: unreachable code generated
			if (state.equals(State.AFTER_JUMP)) {
				assert false;
			} else {
				block.add(s);
			}
			
			// Jump statements terminate basic blocks
			if (s instanceof IRReturn || s instanceof IRJump || s instanceof IRCJump) {
				cfg.blocks.put(block.label, block);
				state = State.AFTER_JUMP;
				
				if (s instanceof IRReturn) {
					cfg.graph.insert(block.label);
				} else if (s instanceof IRJump) {
					IRJump jump = (IRJump) s;
					IRName target = (IRName) jump.target;
					cfg.graph.put(block.label, target.name, null);
				} else {
					IRCJump cjump = (IRCJump) s;
					cfg.graph.put(block.label, cjump.trueLabel, null);
					cfg.graph.put(block.label, cjump.falseLabel, null);
				}
			}
		}
		
		return cfg;
	}
	
	public Block start() {
		return start;
	}
	
	public int size() {
		return blocks.size();
	}
	
	public List<Block> neighbors(Block block) {
		return graph.neighbors(block.label)
			.stream()
			.map(edge -> blocks.get(edge.first))
			.collect(Collectors.toCollection(ArrayList::new));
	}
}
