package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Declare extends Node {

    public Node id;
    public Node type;

    public Declare(Location location, Node id, Node type) {
        this.location = location;
        this.id = id; 
        this.type = type;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
