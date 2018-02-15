package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Variable extends Expression {
    
    public String id;

    public Variable(Location location, String id) {
        this.location = location;
        this.id = id; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
