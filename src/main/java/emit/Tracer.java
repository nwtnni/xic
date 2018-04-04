package emit;

import java.util.TreeSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.*;

/**
 * Reorders execution in an IR AST to eliminate unnecessary IRJump or
 * IRCJump statements.
 * 
 * First splits the sequence into a series of basic blocks, then finds
 * a toplogical sort and greedily chooses longest paths first.
 */
public class Tracer extends IRVisitor<Tracer.Flow> {

    public static IRCompUnit trace(IRCompUnit c) {
        Tracer t = new Tracer();
        c.accept(t);
        return t.compUnit;
    }
    
    /**
     * Class representing a control flow construct in the IR AST.
     */
    static class Flow {
        enum Kind {
            LABEL, JUMP, CJUMP, RETURN
        }

        /**
         * The type of control flow construct.
         */
        public Kind kind;

        /**
         * The label this control flow construct jumps to, or its name, or null if N/A.
         */
        public String t;

        /**
         * The label this control flow construct jumps to when its condition is false,
         * or null if N/A.
         */
        public String f;

        /**
         * Constructor for labels and jumps.
         */
        public Flow(Kind kind, String t) {
            this.kind = kind;
            this.t = t;
            this.f = null;
        }

        /**
         * Constructor for conditional jumps.
         */
        public Flow(String t, String f) {
            this.kind = Kind.CJUMP;
            this.t = t;
            this.f = f;
        }

        /**
         * Constructor for returns.
         */
        public Flow() {
            this.kind = Kind.RETURN;
            this.t = null;
            this.f = null;
        }
    }
    
    public Tracer.Flow visit(IRCompUnit c) {
        functions = new HashMap<>();
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        this.compUnit = new IRCompUnit(c.name, functions);
        return null;
    }
    
    public Tracer.Flow visit(IRFuncDecl fn) {
        fn.body.accept(this);
        functions.put(fn.name, new IRFuncDecl(fn.name, body));
        return null;
    }
    
    public Tracer.Flow visit(IRSeq s) {
        block = new ArrayList<>();
        blocks = new HashMap<>();
        blocks.put(START, null);
        label = START;
        graph = new HashMap<>();
        body = new IRSeq(reorder(s.stmts));
        return null;
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
    
    /**
     * Pseudo-label for the beginning of a function body.
     */
    private static final String START = "_START";

    /**
     * The basic block currently being explored.
     */
    private List<IRNode> block;

    /**
     * The label of the basic block currently being explored.
     * 
     * Null if inside an unreachable statement (e.g. one that follows a jump, but
     * before any labels).
     */
    private String label;

    /**
     * A map from labels to blocks.
     */
    private Map<String, List<IRNode>> blocks;

    /**
     * An adjacency list from block to block.
     */
    private Map<String, List<String>> graph;

    /**
     * Given a list of statements, chops the statements into basic blocks for
     * reordering. Populates the blocks and graph maps.
     * 
     * @param statements
     *            the list of statements to block off.
     */
    public void block(List<IRNode> statements) {
        
        for (IRNode statement : statements) {
            Tracer.Flow flow = statement.accept(this);

            // Not a control flow statement
            if (flow == null) {
                if (label == null) {
                    block.add(statement);
                }
                continue;
            }

            ArrayList<String> next = new ArrayList<>();

            if (flow.kind != Flow.Kind.LABEL) {
                // Not part of a basic block (unreachable)
                if (label == null) {
                    continue;
                } else {
                    // Add control flow to previous block and end it
                    block.add(statement);
                    blocks.put(label, block);
                    block = new ArrayList<>();
                }
            } else {
                // Terminate previous basic block
                if (label != null) {
                    next.add(flow.t);
                    graph.put(label, next);
                    blocks.put(label, block);
                }
                // End previous block and start a new one
                block = new ArrayList<>();
                block.add(statement);
                label = flow.t;
            }

            // Reached a control flow statement
            switch (flow.kind) {
            case CJUMP:
                // Update previous block to point to next two.
                next.add(flow.t);
                next.add(flow.f);
                graph.put(label, next);
                label = null;
                break;
            case JUMP:
                // Update previous block to point to next.
                next.add(flow.t);
                graph.put(label, next);
                label = null;
                break;
            case RETURN:
                // Previous block ends.
                graph.put(label, next);
                label = null;
                break;
            default:
                break;
            }
        }
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

        Flow j = previous.get(previous.size() - 1).accept(this);
        Flow l = next.get(next.size() - 1).accept(this);

        ArrayList<IRNode> merged = new ArrayList<>();
        merged.addAll(previous);

        switch (j.kind) {
        case RETURN:
        case LABEL:
            break;
        case JUMP:
            // Remove extra jump
            if (j.t.equals(l.t)) {
                merged.remove(merged.size() - 1);
            }
            break;
        case CJUMP:
            // Flip condition and fall through on false
            if (j.t.equals(l.t)) {
                IRCJump jump = (IRCJump) merged.remove(merged.size() - 1);
                IRConst one = new IRConst(1);
                IRBinOp lneg = new IRBinOp(IRBinOp.OpType.XOR, one, jump.cond);
                merged.add(new IRCJump(lneg, j.f));
            }
            // Fall through on false
            else if (j.f.equals(l.t)) {
                IRCJump jump = (IRCJump) merged.remove(merged.size() - 1);
                merged.add(new IRCJump(jump.cond, j.t));
            }
            break;
        }
        merged.addAll(next);
        return merged;
    }
    
    /**
     * Basic helper class for DFS.
     */
    class Node {
        public int depth;
        public Node previous;
        public String label;
        public Node(int depth, Node previous, String label) {
            this.depth = depth;
            this.previous = previous;
            this.label = label;
        }
    }

    /**
     * Greedily reorders the given list of IRNode statements by
     * finding a topological sort.
     */
    public List<IRNode> reorder(List<IRNode> statements) {
        // Partition statements into basic blocks
        block(statements);
        
        // Too small to reorder
        if (blocks.size() <= 1) { return statements; }
        
        TreeSet<String> unmarked = new TreeSet<String>(graph.keySet());
        unmarked.remove(START);
        String start = START;
        
        // Topologically sorted blocks
        TreeSet<Node> sorted = new TreeSet<Node>((a, b) -> b.depth - a.depth);
        
        // Final set of condensed blocks to condense
        List<List<IRNode>> condensed = new ArrayList<>();
        
        // Stack for DFS
        TreeSet<Node> stack = new TreeSet<>((a, b) -> b.depth - a.depth);
        TreeSet<String> visited = new TreeSet<>();
        Node first = new Node(0, null, start);
        stack.add(first);
        
        // Run trace on first unordered block
        while (stack.size() > 0) {
            Node node = stack.pollFirst();
            visited.add(node.label);
            
            // Explore neighbors
            for (String nextLabel : graph.get(node.label)) {
                if (visited.contains(nextLabel)) { continue; }
                Node next = new Node(node.depth + 1, node, nextLabel);
                stack.add(next);
                sorted.add(next);
            }
        }

        // While there are unordered blocks, condense the longest paths first
        while (unmarked.size() > 0) {
            Node longest = sorted.stream()
                    .filter(n -> unmarked.contains(n.label))
                    .findFirst()
                    .get();
            
            List<List<IRNode>> condense = new ArrayList<>();
            for (Node node = longest; node != null; node = node.previous) {
                unmarked.remove(node.label);
                condense.add(blocks.get(node.label));
            }
            
            condensed.add(
                condense
                    .stream()
                    .reduce(new ArrayList<>(), (a, b) -> merge(a, b)
                )
            );
        }
        
        // Condense the resulting paths
        return condensed.stream().reduce(new ArrayList<>(), (a, b) -> merge(a, b));
    }

    /**
     * Encountered label.
     */
    public Tracer.Flow visit(IRLabel l) {
        return new Flow(Flow.Kind.LABEL, l.name);
    }

    /**
     * Encountered conditional jump statement.
     */
    public Tracer.Flow visit(IRCJump j) {
        return new Flow(j.trueLabel, j.falseLabel);
    }

    /**
     * Encountered top-level jump statement.
     * 
     * Requires IRJump.target is an IRName (i.e. label)
     */
    public Tracer.Flow visit(IRJump j) {
        return j.target.accept(this);
    }

    /**
     * Encountered jump statement.
     */
    public Tracer.Flow visit(IRName n) {
        return new Flow(Flow.Kind.JUMP, n.name);
    }

    /**
     * Encountered return statement.
     */
    public Tracer.Flow visit(IRReturn r) {
        return new Flow();
    }
}
