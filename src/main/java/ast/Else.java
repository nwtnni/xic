package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

public class Else extends Node {

    public Node block;

    public Else(Location location, Node block) { 
        this.location = location;
        this.block = block;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}

