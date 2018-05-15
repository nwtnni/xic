package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import xic.XicException;

// A AST global variable declaration node.
public class XiGlobal extends TopDeclr {
    public Stmt stmt;

    public XiGlobal(Location location, Node declr) {
        this.location = location;
        this.stmt = (Stmt) declr;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
