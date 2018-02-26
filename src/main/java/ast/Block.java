package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

import java.util.ArrayList;

// A Block of statements
public class Block extends Node {
    
    public enum Kind {
        RETURN, NO_RETURN
    }

    public Kind kind;
    public ArrayList<Node> statements;
    public Node returns;

    public Block(Location location, ArrayList<Node> statements) {
        this.kind = Kind.NO_RETURN;
        this.location = location;
        this.statements = statements;
        this.returns = null;
    }

    public Block(ArrayList<Node> statements) {
        this.kind = Kind.NO_RETURN;
        this.statements = statements;
        this.returns = null;
    }

    public Block(ArrayList<Node> statements, Node returns) {
        this.kind = Kind.RETURN;
        this.statements = statements;
        this.returns = returns;
    }

    public boolean hasReturn() {
        return kind == Kind.RETURN; 
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
