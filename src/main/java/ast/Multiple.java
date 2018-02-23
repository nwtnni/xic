package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Multiple extends Node {

    public enum Kind {
        ASSIGN, RETURN 
    }

    public Kind kind;
    public ArrayList<Node> values;

    public Multiple(Location location, ArrayList<Node> values, Kind kind) {
        this.kind = kind;
        this.location = location;
        this.values = values;
    }

    public boolean isAssign() {
        return kind == Kind.ASSIGN; 
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
