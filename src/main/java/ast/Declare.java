package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A Variable Declaration
public class Declare extends Node {

    public enum Kind {
        VAR, UNDERSCORE 
    }

    private Kind kind;
    public String id;
    public Node xiType;

    public Declare(Location location, String id, Node type) {
        this.kind = Kind.VAR;
        this.location = location;
        this.id = id; 
        this.xiType = type;
    }

    // Used for underscores
    public Declare(Location location) {
        this.kind = Kind.UNDERSCORE;
        this.location = location;
        this.id = null;
        this.xiType = null;
    }

    public boolean isUnderscore() {
        return kind == Kind.UNDERSCORE; 
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
