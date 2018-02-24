package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Declare extends Node {

    public enum Kind {
        VAR, UNDERSCORE 
    }

    private Kind kind;
    public Node id;
    public Node type;

    public Declare(Location location, Node id, Node type) {
        this.kind = Kind.VAR;
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

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
