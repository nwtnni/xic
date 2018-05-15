package parse;

import java.util.List;

import ast.Node;
import util.Either;

/** Either type of single or multiple AST nodes. */
public class MultipleNode extends Either<Node, List<Node>> {

    public MultipleNode(Node single, List<Node> multiple) {
        super(single, multiple);
    }
    
    public static MultipleNode of(Node single) {
        return new MultipleNode(single, null);
    }

    public static MultipleNode of(List<Node> multiple) {
        return new MultipleNode(null, multiple);
    }

    public boolean isSingle() {
        return super.isLeft();
    }

    public boolean isMultiple() {
        return super.isRight();
    }

    @Deprecated
    public boolean isLeft() {
        assert false;
        return false;
    }

    @Deprecated
    public boolean isRight() {
        assert false;
        return false;
    }

    public Node getSingle() {
        return super.getLeft();
    }

    public List<Node> getMultiple() {
        return super.getRight();
    }

    @Deprecated
    public Node getLeft() {
        assert false;
        return null;
    }

    @Deprecated
    public List<Node> getRight() {
        assert false;
        return null;
    }
}