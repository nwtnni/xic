package parser;

import java.util.ArrayList;

public class Type extends Node {

    public static final Type INTEGER = new Type(Primitive.INTEGER);
    public static final Type BOOLEAN = new Type(Primitive.BOOLEAN);

    public enum Primitive {
        ARRAY,
        BOOLEAN,
        INTEGER;

        public String toString() {
            switch (this) {
                case ARRAY: return "[]";
                case BOOLEAN: return "bool";
                case INTEGER: return "int";
            }
            assert false;
            return "";
        }
    }

    public Primitive primitive;
    public Node size;
    public Node child;

    private Type(Primitive primitive) {
        assert primitive != Primitive.ARRAY;
        this.primitive = primitive; 
        this.child = null;
        this.size = null;
    }

    public Type(Node child, Node size) {
        this.primitive = Primitive.ARRAY;
        this.child = child;
        this.size = size;
    }

    public Type(Node child) {
        this.primitive = Primitive.ARRAY; 
        this.child = child;
        this.size = null;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
