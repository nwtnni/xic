package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A return statement
public class XiReturn extends Node {

    public enum Kind {
        VALUE, VOID  
    }

    public Kind kind;
    public List<Node> values;

    public XiReturn(Location location, List<Node> values) {
        this.kind = Kind.VALUE;
        this.location = location;
        this.values = values;
    }

    public XiReturn(Location location) {
        this.kind = Kind.VOID; 
        this.location = location;
        this.values = null;
    }

    public boolean hasValues() {
        return kind == Kind.VALUE; 
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
