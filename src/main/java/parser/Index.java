package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Index extends Expression {
    
    public Node array;
    public Node index;

    public Index(Location location, Node array, Node index) {
        this.location = location;
        this.array = array; 
        this.index = index;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
