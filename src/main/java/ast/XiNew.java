package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A unary operator
public class XiNew extends Expr {

    public Node expr; 

    public XiNew(Location location, Node expr) {
        this.location = location;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }

}
