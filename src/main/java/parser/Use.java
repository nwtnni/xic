package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Use extends Node {
    
    public String file;

    public Use(Location location, String file) {
        this.location = location;
        this.file = file; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
