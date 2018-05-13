package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;

import xic.XicException;

// A AST class node.
public class XiClass extends TopDeclr {
    public String id;
    public String parent;

    public List<Node> body;

    public XiClass(Location location, String id, List<Node> body) {
        this.location = location;
        this.id = id; 
        this.body = body;
    }

    public XiClass(Location location, String id, String parent, List<Node> body) {
        this.location = location;
        this.id = id;
        this.parent = parent;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
