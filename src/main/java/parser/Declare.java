package parser;

public class Declare extends Statement {

    public static final Declare UNDERSCORE = new Declare("_", Type.INTEGER);

    public String id;
    public Type type;

    public Declare(String id, Type type) {
        this.id = id; 
        this.type = type;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
