package parser;

public class Variable extends Expression {

    public static final Variable UNDERSCORE = new Variable("_");
    
    private String id;

    public Variable(String id) {
        this.id = id; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
