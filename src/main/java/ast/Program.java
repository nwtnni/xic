package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

import java.util.ArrayList;

// The entire program
public class Program extends Node {

    public enum Kind {
        PROGRAM, INTERFACE 
    }

    public Kind kind;
    public ArrayList<Node> uses;
    public ArrayList<Node> fns;

    public Program(Location location, ArrayList<Node> uses, ArrayList<Node> fns) {
        this.kind = Kind.PROGRAM;
        this.location = location;
        this.uses = uses;
        this.fns = fns;
    }

    public Program(Location location, ArrayList<Node> fns) {
        this.kind = Kind.INTERFACE;
        this.location = location;
        this.uses = null;
        this.fns = fns;
    }

    public boolean isProgram() {
        return kind == Kind.PROGRAM; 
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
