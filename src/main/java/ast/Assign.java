package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

public class Assign extends Node {
    
    public Node lhs;
    public Node rhs;

    public Assign(Location location, Node lhs, Node rhs) {
        this.location = location;
        this.lhs = lhs; 
        this.rhs = rhs;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
