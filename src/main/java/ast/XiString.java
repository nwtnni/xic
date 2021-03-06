package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// An Xi String
public class XiString extends Expr {

    public String escaped;
    public List<Long> value;

    public XiString(Location location, String escaped, List<Long> value) {
        this.location = location;
        this.escaped = escaped;
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
