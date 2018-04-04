package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An Xi type
public class XiType extends Node {

    public enum Kind {
        ARRAY, CLASS
    }

    public Kind kind;
    public Node size;
    public Node child;
    public String id;

    public XiType(Location location, String id) {
        this.location = location;
        this.kind = Kind.CLASS; 
        this.id = id;
        this.child = null;
        this.size = null;
    }

    public XiType(Location location, Node child, Node size) {
        this.location = location;
        this.kind = Kind.ARRAY;
        this.id = null;
        this.child = child;
        this.size = size;
    }

    public XiType(Location location, Node child) {
        this.location = location;
        this.kind = Kind.ARRAY; 
        this.id = null;
        this.child = child;
        this.size = null;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }

    public boolean isClass() {
        return kind == Kind.CLASS; 
    }
    
    public boolean hasSize() {
        return size != null;
    }
}
