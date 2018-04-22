package reorder;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultDirectedGraph;

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

import util.Pair;
import util.PairEdge;

public class ControlFlow {

	private class LabelPair extends Pair<String, String> {
		public LabelPair(String a, String b) {
			super(a, b);
		}
	}

	private class LabelPairFactory implements EdgeFactory<String, LabelPair> {
		@Override
		public LabelPair createEdge(String a, String b) {
			return new LabelPair(a, b);
		}
	}

	private ControlFlow() {
		graph = new DefaultDirectedGraph<>(new LabelPairFactory());
		blocks = new HashMap<>();
	}
	
	private Block start;
	private Graph<String, LabelPair> graph;
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
		cfg.graph.addVertex(cfg.start.label);
		
		for (IRNode s : statements) {
			
			if (s instanceof IRLabel) {
				IRLabel label = (IRLabel) s;
				switch (state) {
				case WITHIN_BLOCK:
					block.add(new IRJump(new IRName(label.name)));
					cfg.blocks.put(block.label, block);
					cfg.graph.addVertex(block.label);
					cfg.graph.addVertex(label.name);
					cfg.graph.addEdge(block.label, label.name);
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
				continue;
			} else {
				block.add(s);
			}
			
			// Jump statements terminate basic blocks
			if (s instanceof IRReturn || s instanceof IRJump || s instanceof IRCJump) {
				cfg.blocks.put(block.label, block);
				cfg.graph.addVertex(block.label);

				state = State.AFTER_JUMP;
				
				if (s instanceof IRReturn) {
					cfg.graph.addVertex(block.label);
				} else if (s instanceof IRJump) {
					IRJump jump = (IRJump) s;
					IRName target = (IRName) jump.target;
					cfg.graph.addEdge(block.label, target.name);
				} else {
					IRCJump cjump = (IRCJump) s;
					cfg.graph.addEdge(block.label, cjump.trueName());
					cfg.graph.addEdge(block.label, cjump.falseName());
				}
			}
		}
		
		cfg.blocks.put(block.label, block);
		return cfg;
	}
	
	public Block start() {
		return start;
	}
	
	public int size() {
		return blocks.size();
	}
	
	public Set<Block> blocks() {
		return new HashSet<>(blocks.values());
	}
	
	public List<Block> neighbors(Block block) {
		return graph.outgoingEdgesOf(block.label)
			.stream()
			.map(edge -> blocks.get(edge.first))
			.collect(Collectors.toCollection(ArrayList::new));
	}
}
