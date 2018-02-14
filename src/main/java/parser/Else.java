package parser;

public class Else extends Statement {

    public Node block;

    public Else(Node block) { 
        this.block = block;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}

