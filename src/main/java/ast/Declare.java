package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Declare extends Node {

    private enum Kind {
        VARIABLE, UNDERSCORE 
    }

    private Kind kind;
    public Node id;
    public Node type;

    public Declare(Location location, Node id, Node type) {
        this.kind = Kind.VARIABLE;
        this.location = location;
        this.id = id; 
        this.type = type;
    }

    public Declare(Location location) {
        this.kind = Kind.UNDERSCORE;
        this.location = location;
        this.id = null;
        this.type = null;
    }

    public boolean isUnderscore() {
        return kind == Kind.UNDERSCORE; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
