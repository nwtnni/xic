package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

import java.util.ArrayList;

public class Call extends Node {
    
    public String id;
    public ArrayList<Node> args; 
    
    public Call(Location location, String id, ArrayList<Node> args) {
        this.location = location;
        this.id = id; 
        this.args = args;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
