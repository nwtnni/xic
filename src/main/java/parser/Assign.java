package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Assign extends Statement {
    
    public Node lhs;
    public Node rhs;

    public Assign(Location location, Node lhs, Node rhs) {
        this.location = location;
        this.lhs = lhs; 
        this.rhs = rhs;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
