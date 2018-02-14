package parser;

public class Variable extends Expression {
    
    private String id;

    public Variable(String id) {
        this.id = id; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
