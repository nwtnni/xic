package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An Xi primitive boolean
public class XiBool extends Expr {

    public boolean value;

    public XiBool(Location location, boolean value) {
        this.location = location;
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
