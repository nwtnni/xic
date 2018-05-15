package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A unary operator
public class XiNew extends Expr {

    public String name; 

    public XiNew(Location location, String name) {
        this.location = location;
        this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }

}
