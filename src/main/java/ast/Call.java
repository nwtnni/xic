package ast;

import java.util.List;
import java.util.ArrayList;

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

    public Call(Location location, String id, Node arg) {
        this.location = location;
        this.id = id;
        this.args = new ArrayList<>();
        args.add(arg);
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }

    public List<Node> getArgs() {
        return args;
    }
}
