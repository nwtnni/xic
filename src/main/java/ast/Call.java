package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A Function Call
public class Call extends Node {
    
    public String id;
    public List<Node> args; 
    
    public Call(Location location, String id, List<Node> args) {
        this.location = location;
        this.id = id; 
        this.args = args;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }

    public List<Node> getArgs() {
        return args;
    }
}
