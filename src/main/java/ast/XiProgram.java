package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// The entire program
public class XiProgram extends Node {

    public enum Kind {
        PROGRAM, INTERFACE;
    }

    public Kind kind;
    public List<Node> uses;
    public List<Node> body;

    public XiProgram(Location location, Kind kind, List<Node> uses, List<Node> fns) {
        this.kind = kind;
        this.location = location;
        this.uses = uses;
        this.body = fns;
    }

    public boolean isProgram() {
        return kind == Kind.PROGRAM; 
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
