package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A variable identifier
public class Var extends Node {
    
    public String id;

    public Var(Location location, String id) {
        this.location = location;
        this.id = id; 
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
