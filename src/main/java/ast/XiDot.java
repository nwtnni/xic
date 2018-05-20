package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// Binary Operation
public class XiDot extends Expr {

    public Node lhs;
    public Node rhs;

    public XiDot(Location location, Node lhs, Node rhs) {
        this.location = location;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
