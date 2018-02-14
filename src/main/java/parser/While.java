package parser;

public class While extends Statement {

    public Node guard;
    public Node block;

    public While(Node guard, Node block) {
        this.guard = guard;
        this.block = block;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
