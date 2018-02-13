package parser;

public class Variable extends Expression {
    
    private String id;

    public Variable(String id) {
        this.id = id; 
    }

    public String toString() {
        return id; 
    }
}
