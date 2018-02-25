package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

public class While extends Node {

    public Node guard;
    public Node block;

    public While(Location location, Node guard, Node block) {
        this.location = location;
        this.guard = guard;
        this.block = block;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
