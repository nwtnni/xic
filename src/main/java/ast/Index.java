package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// Used for Array Calls (eg a[0][1])
public class Index extends Node {
    
    public Node array; //The "name" of the array
    public Node index;

    public Index(Location location, Node array, Node index) {
        this.location = location;
        this.array = array; 
        this.index = index;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
