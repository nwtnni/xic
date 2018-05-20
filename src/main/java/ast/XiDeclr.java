package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A Variable Declaration
public class XiDeclr extends Stmt {

    public enum Kind {
        VAR, UNDERSCORE 
    }

    private Kind kind;
    public String id;
    public XiType xiType;

    public XiDeclr(Location location, String id, Node type) {
        this.kind = Kind.VAR;
        this.location = location;
        this.id = id; 
        this.xiType = (XiType) type;
    }

    // Used for underscores
    public XiDeclr(Location location) {
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
