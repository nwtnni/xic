package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Else extends Node {

    public Node block;

    public Else(Location location, Node block) { 
        this.location = location;
        this.block = block;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}

