package ast;

import java.util.ArrayList;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

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

    public ArrayList<Node> getArgs() {
        if (args instanceof Multiple) {
            return ((Multiple) args).values;
        } else {
            ArrayList<Node> argList = new ArrayList<>();
            argList.add(args);
            return argList;
        }
    }
}
