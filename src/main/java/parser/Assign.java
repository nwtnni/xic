package parser;

public class Assign extends Statement {
    
    public Node lhs;
    public Node rhs;

    public Assign(Node lhs, Node rhs) {
        this.lhs = lhs; 
        this.rhs = rhs;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
