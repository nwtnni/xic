package ir;

import java.util.Arrays;
import java.util.List;

/** 
 * A dummy node to wrap a list of nodes to be passed 
 * around by the visitor.
 */
public class IRNodeList extends IRNode {
    protected List<IRNode> nodes;

    /**
     * @param nodes values contained in this list
     */
    public IRNodeList(IRNode... nodes) {
        this(Arrays.asList(nodes));
    }

    /**
     * @param nodes values contained in this list
     */
    public IRNodeList(List<IRNode> nodes) {
        this.nodes = nodes;
    }

    public boolean isEmpty() {
        return nodes.size() == 1;
    }

    public boolean hasMultiple() {
        return nodes.size() > 1;
    }

    public IRNode getNode() {
        if (!isEmpty()) {
            return nodes.get(0);
        }
        return null;
    }

    public List<IRNode> nodes() {
        return nodes;
    } 

    @Override
    public String label() {
        return "INTERNAL_LIST";
    }

    /**
     * IRNodeList should never be visited.
     */
    @Override
    public <T> T accept(IRVisitor<T> v) {
        assert false;
        return null;
    }

}