package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class XiType extends Node {

    public enum Kind {
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

    public Kind kind;
    public Node size;
    public Node child;

    public XiType(Location location, Kind kind) {
        assert kind != Kind.ARRAY;
        this.location = location;
        this.kind = kind; 
        this.child = null;
        this.size = null;
    }

    public XiType(Location location, Node child, Node size) {
        this.location = location;
        this.kind = Kind.ARRAY;
        this.child = child;
        this.size = size;
    }

    public XiType(Location location, Node child) {
        this.location = location;
        this.kind = Kind.ARRAY; 
        this.child = child;
        this.size = null;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
