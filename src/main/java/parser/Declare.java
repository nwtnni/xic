package parser;

public class Declare extends Statement {

    public Node id;

    public Declare(Node id, Type type) {
        this.id = id; 
        this.type = type;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
