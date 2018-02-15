package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class While extends Node {

    public Node guard;
    public Node block;

    public While(Location location, Node guard, Node block) {
        this.location = location;
        this.guard = guard;
        this.block = block;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
