package reorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
					cfg.graph.put(block.label, label.name, null);
                    cfg.blocks.put(block.label, block);
					break;
				case AFTER_JUMP:
					state = State.WITHIN_BLOCK;
					break;
				}
				block = new Block(label);
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
	
	public int height(Block block) {
		return graph.height(block.label);
	}
	
	public Set<Block> blocks() {
		return new HashSet<>(blocks.values());
	}
	
	public List<Block> neighbors(Block block) {
		return graph.neighbors(block.label)
			.stream()
			.map(edge -> blocks.get(edge.first))
			.collect(Collectors.toCollection(ArrayList::new));
	}
}
