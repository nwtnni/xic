package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Program extends Node {

    public enum Kind {
        PROGRAM, INTERFACE 
    }

    public Kind kind;
    public ArrayList<Node> uses;
    public ArrayList<Node> functions;

    public Program(Location location, ArrayList<Node> uses, ArrayList<Node> functions) {
        this.kind = Kind.PROGRAM;
        this.location = location;
        this.uses = uses;
        this.functions = functions;
    }

    public Program(Location location, ArrayList<Node> functions) {
        this.kind = Kind.INTERFACE;
        this.location = location;
        this.uses = null;
        this.functions = functions;
    }

    public boolean isProgram() {
        return kind == Kind.PROGRAM; 
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
