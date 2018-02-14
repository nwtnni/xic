package parser;

public class If extends Statement {

    public Node guard;
    public Node block;

    public If(Node guard, Node block) {
        this.guard = guard; 
        this.block = block;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
