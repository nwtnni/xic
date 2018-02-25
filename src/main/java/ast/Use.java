package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

public class Use extends Node {
    
    public String file;

    public Use(Location location, String file) {
        this.location = location;
        this.file = file; 
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
