package reorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.PriorityQueue;

import ir.*;

/**
 * Reorders execution in an IR AST to eliminate unnecessary IRJump or
 * IRCJump statements.
 * 
 * First splits the sequence into a series of basic blocks, then finds
 * a toplogical sort and greedily chooses longest paths first.
 */
public class Tracer extends IRVisitor<Void> {

    public static IRCompUnit trace(IRCompUnit c) {
        Tracer t = new Tracer();
        c.accept(t);
        return t.compUnit;
    }
    
    /**
     * Reordered compilation unit.
     */
    private IRCompUnit compUnit;
    
    /**
     * Reordered functions.
     */
    private Map<String, IRFuncDecl> functions;

    /**
     * Reordered function body.
     */
    private IRNode body;
    
    public Void visit(IRCompUnit c) {
        functions = new HashMap<>();
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        this.compUnit = new IRCompUnit(c.name, functions);
        return null;
    }
    
    public Void visit(IRFuncDecl fn) {
        fn.body.accept(this);
        functions.put(fn.name, new IRFuncDecl(fn.name, body));
        return null;
    }
    
    public Void visit(IRSeq s) {
        body = new IRSeq(reorder(s.stmts));
        return null;
    }

    /**
     * Merges two blocks together. Removes unnecessary IRJump or IRCJump nodes when
     * possible.
     */
    public List<IRNode> merge(List<IRNode> previous, List<IRNode> next) {
    	
        if (previous.isEmpty()) {
            return next;
        }
        if (next.isEmpty()) {
            return previous;
        }

        IRNode jump = previous.get(previous.size() - 1);
        
        // Debugging: basic blocks must begin with a label
        assert next.get(0) instanceof IRLabel;
        IRLabel label = (IRLabel) next.get(0);
        
        ArrayList<IRNode> merged = new ArrayList<>();
        merged.addAll(previous);

        if (jump instanceof IRJump) {
        	IRName target = (IRName) ((IRJump) jump).target;

        	// Remove extra jump
            if (target.name.equals(label.name)) {
                merged.remove(merged.size() - 1);
            }
            
        } else if (jump instanceof IRCJump) {
        	
        	IRCJump cjump = (IRCJump) jump;
            
        	// Flip condition and fall through on false
            if (cjump.trueName().equals(label.name)) {
                IRConst one = new IRConst(1);
                IRBinOp lneg = new IRBinOp(IRBinOp.OpType.XOR, one, cjump.cond);
                
                merged.set(merged.size() - 1, new IRCJump(lneg, cjump.falseName()));
                next.remove(0);
            }
            
            // Fall through on false
            else if (cjump.falseName().equals(label.name)) {
                merged.set(merged.size() - 1, new IRCJump(cjump.cond, cjump.trueName()));
                next.remove(0);
            }
        }

        merged.addAll(next);
        return merged;
    }

    /**
     * Reorders the list of statements by repeatedly finding traces.
     */
    public List<IRNode> reorder(List<IRNode> statements) {
    	
    	ControlFlow cfg = ControlFlow.from(statements);
    	
        PriorityQueue<Block> unmarked = new PriorityQueue<>((a, b) -> cfg.height(b) - cfg.height(a));
    	unmarked.addAll(cfg.blocks());

    	List<List<IRNode>> traces = new ArrayList<>();
    	
    	while (!unmarked.isEmpty()) {
    		
    		Block node = unmarked.poll();

    		while (true) {
    			traces.add(new ArrayList<>(node.statements));
    			unmarked.remove(node);
    			Optional<Block> next = cfg.neighbors(node)
    				.stream()
    				.filter(block -> unmarked.contains(block))
    				.sorted(unmarked.comparator())
    				.findFirst();
    			
    			if (!next.isPresent()) break;
    			else node = next.get();
    		}
    	}
    	
    	return traces.stream().reduce((a, b) -> merge(a, b)).get();
    }
}
