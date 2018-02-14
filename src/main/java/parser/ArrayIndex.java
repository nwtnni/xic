package parser;

public class ArrayIndex extends Expression {
    
    public Node array;
    public Node index;

    public ArrayIndex(Node array, Node index) {
        this.array = array; 
        this.index = index;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
