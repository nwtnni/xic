package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import xic.XicException;

// A wrapper for a statement used as an expression.
// Primarily for nesting declares inside of assignments.
public class XiExprStmt extends Expr {
    
    public Stmt stmt;

    public XiExprStmt(Location location, Node stmt) {
        this.location = location;
        this.stmt = (Stmt) stmt;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
