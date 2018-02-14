package parser;

public class While extends Statement {

    public Node block;

    public While(Node block) {
        this.block = block;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
