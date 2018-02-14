package parser;

import java.util.ArrayList;

public class Type {

    public static final Type INTEGER = new Type(Primitive.INTEGER);
    public static final Type BOOLEAN = new Type(Primitive.BOOLEAN);
    public static final Type UNIT = new Type(Primitive.UNIT);

    public enum Primitive {
        ARRAY,
        BOOLEAN,
        INTEGER,
        MULTIPLE, 
        UNIT
    }

    private Primitive primitive;
    private ArrayList<Type> children;

    private Type(Primitive primitive) {
        assert primitive != Primitive.ARRAY;
        this.primitive = primitive; 
        this.children = null;
    }

    public Type(Primitive primitive, ArrayList<Type> children) {
        assert primitive == Primitive.ARRAY || primitive == Primitive.MULTIPLE;
        this.primitive = primitive;
        this.children = children;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Type)) { return false; }
        Type t = (Type) other;
        
        if (!primitive.equals(Primitive.ARRAY)
        || !primitive.equals(t.primitive)
        || children.size() != t.children.size()) { return false; }

        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).equals(t.children.get(i))) {
                return false; 
            }
        }

        return true;
    }

    public int hashCode() {
        switch (primitive) {
            case INTEGER: return 2;
            case BOOLEAN: return 3;
            case ARRAY: {
                int product = 4; 
                for (Type child : children) {
                    product *= child.hashCode(); 
                }
                return product;
            }
        }
        assert false;
        return 0;
    }
}
