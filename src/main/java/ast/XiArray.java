package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

import java.util.List;

// A Xi array
public class XiArray extends Expr {

    public List<Node> values;

    public XiArray(Location location, List<Node> values) {
        this.location = location;
        this.values = values;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
