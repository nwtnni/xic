package parser;

import java.util.ArrayList;

public class Type {

    public static final Type INTEGER = new Type(Primitive.INTEGER);
    public static final Type BOOLEAN = new Type(Primitive.BOOLEAN);

    public enum Primitive {
        INTEGER, BOOLEAN, ARRAY
    }

    private Primitive primitive;
    private Type child;
    private int length;

    private Type(Primitive primitive) {
        assert primitive != Primitive.ARRAY;
        this.primitive = primitive; 
        this.length = 0;
        this.child = null;
    }

    public Type(Type type, int length) {
        this.primitive = Primitive.ARRAY;
        this.length = length;
        this.child = null;
        type.child = this;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Type)) { return false; }
        Type t = (Type) other;

        if (!(primitive.equals(t.primitive))) {
            return false; 
        } else if (primitive.equals(Primitive.ARRAY)) {
            if (length == 0 || t.length == 0) {
                return child.equals(t.child);
            } else {
                return length == t.length && child.equals(t.child);
            }
        } else {
            return true; 
        }
    }

    public int hashCode() {
        switch (primitive) {
            case INTEGER: return 2;
            case BOOLEAN: return 3;
            case ARRAY: return this.length * child.hashCode();
        }
        assert false;
        return 0;
    }
}
