package ast;

import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// The entire program
public class Program extends Node {

    public enum Kind {
        PROGRAM, INTERFACE 
    }

    public Kind kind;
    public List<Node> uses;
    public List<Node> body;

    public Program(Location location, List<Node> uses, List<Node> fns) {
        this.kind = Kind.PROGRAM;
        this.location = location;
        this.uses = uses;
        this.body = fns;
    }

    public Program(Location location, List<Node> fns) {
        this.kind = Kind.INTERFACE;
        this.location = location;
        this.uses = null;
        this.body = fns;
    }

    public boolean isProgram() {
        return kind == Kind.PROGRAM; 
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
