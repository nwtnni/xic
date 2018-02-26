package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

import java.util.ArrayList;

// A Function Call
public class Call extends Node {
    
    public String id;
    public Node args; 
    
    public Call(Location location, String id, Node args) {
        this.location = location;
        this.id = id; 
        this.args = args;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
