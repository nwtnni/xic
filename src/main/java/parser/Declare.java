package parser;

public class Declare extends Statement {

    private String id;
    private Type type;

    public static final Declare UNDERSCORE = new Declare("_", Type.INTEGER);

    public Declare(String id, Type type) {
        this.id = id; 
        this.type = type;
    }
    
    public String toString() {
        if (this == UNDERSCORE) {
            return "_";
        } else {
            return id + " " + type.toString();
        }
    }
}
