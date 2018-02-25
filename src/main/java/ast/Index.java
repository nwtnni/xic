package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

public class Index extends Node {
    
    public Node array;
    public Node index;

    public Index(Location location, Node array, Node index) {
        this.location = location;
        this.array = array; 
        this.index = index;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
