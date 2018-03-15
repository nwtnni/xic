package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A return statement
public class Return extends Node {

    public enum Kind {
        VALUE, VOID  
    }

    public Kind kind;
    public List<Node> values;

    public Return(Location location, List<Node> values) {
        this.kind = Kind.VALUE;
        this.location = location;
        this.values = values;
    }

    public Return(Location location) {
        this.kind = Kind.VOID; 
        this.location = location;
        this.values = null;
    }

    public boolean hasValues() {
        return kind == Kind.VALUE; 
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
