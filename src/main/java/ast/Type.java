package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Type extends Node {

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

    public Type(Location location, Primitive primitive) {
        assert primitive != Primitive.ARRAY;
        this.location = location;
        this.primitive = primitive; 
        this.child = null;
        this.size = null;
    }

    public Type(Location location, Node child, Node size) {
        this.location = location;
        this.primitive = Primitive.ARRAY;
        this.child = child;
        this.size = size;
    }

    public Type(Location location, Node child) {
        this.location = location;
        this.primitive = Primitive.ARRAY; 
        this.child = child;
        this.size = null;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
