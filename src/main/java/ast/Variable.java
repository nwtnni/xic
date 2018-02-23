package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Variable extends Node {
    
    public String id;

    public Variable(Location location, String id) {
        this.location = location;
        this.id = id; 
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
