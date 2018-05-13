package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import xic.XicException;

// A AST global variable declaration node.
public class XiGlobal extends TopDeclr {
    public XiDeclr declr;

    public XiGlobal(Location location, Node declr) {
        this.location = location;
        this.declr = (XiDeclr) declr;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
