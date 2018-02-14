package parser;

public class Index extends Expression {
    
    public Node array;
    public Node index;

    public Index(Node array, Node index) {
        this.array = array; 
        this.index = index;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
