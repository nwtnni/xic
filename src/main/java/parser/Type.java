package parser;

import java.util.ArrayList;

public class Type {

    private Primitive type;
    private ArrayList<Integer> shape;

    public enum Primitive { INT, BOOL }

    public static final Type INT = new Type(Primitive.INT);
    public static final Type BOOL = new Type(Primitive.BOOL);

    public Type(Primitive type) {
        shape = new ArrayList<Integer>();
        shape.add(1);
        this.type = type; 
        this.shape = shape;
    }

    public Type(Primitive type, int length) {
        shape = new ArrayList<Integer>();
        shape.add(length);
        this.type = type; 
        this.shape = shape;
    }

    public void promote(int length) {
        this.shape.add(0, length);
    }

    public boolean equals(Object other) {
        if (!(other instanceof Type)) {
            return false; 
        }

        Type t = (Type) other;

        if (this.type != t.type 
        || (this.shape != null && t.shape != null
        && this.shape.size() != t.shape.size())) {
            return false;
        }
    
        for (int i = 0; i < this.shape.size(); i++) {
            if (this.shape.get(i) != t.shape.get(i)) {
                return false; 
            }
        }

        return true;
    }

    public int hashCode() {
        int hash;
        if (this.type == Primitive.INT) {
            hash = -100; 
        } else {
            hash = 100; 
        }
        if (this.shape != null) {
            for (int i = 0; i < this.shape.size(); i++) {
                hash *= shape.get(i);
            }
        }
        return hash;
    }
}
