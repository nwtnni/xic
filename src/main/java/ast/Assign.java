package ast;

import java.util.List;
import java.util.ArrayList;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// Assign Statement
public class Assign extends Node {
    
    public List<Node> lhs;
    public Node rhs;

    public Assign(Location location, List<Node> lhs, Node rhs) {
        this.location = location;
        this.lhs = lhs; 
        this.rhs = rhs;
    }

    public Assign(Location location, Node lhs, Node rhs) {
        this.location = location;
        this.lhs = new ArrayList<Node>();
        this.lhs.add(lhs);
        this.rhs = rhs;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
