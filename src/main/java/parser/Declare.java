package parser;

public class Declare extends Statement {

    public Node id;
    public Node type;

    public Declare(Node id, Node type) {
        this.id = id; 
        this.type = type;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
