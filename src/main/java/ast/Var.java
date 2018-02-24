package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Var extends Node {
    
    public String id;

    public Var(Location location, String id) {
        this.location = location;
        this.id = id; 
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
