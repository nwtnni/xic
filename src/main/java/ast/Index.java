package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// Used for Array Calls (eg a[0][1])
public class Index extends Node {
    
    public Node array; //The "name" of the array
    public Node index;
    public boolean isExpr; // on RHS

    public Index(Location location, Node array, Node index, boolean isExpr) {
        this.location = location;
        this.array = array; 
        this.index = index;
        this.isExpr = isExpr;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
