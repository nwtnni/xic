package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An Xi character
public class XiChar extends Expr {

    public String escaped;
    public long value;

    public XiChar(Location location, String escaped, long value) {
        this.location = location;
        this.escaped = escaped;
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
