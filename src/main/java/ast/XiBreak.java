package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An Xi primitive boolean
public class XiBreak extends Stmt {

    public XiBreak(Location location) {
        this.location = location;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
