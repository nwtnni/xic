package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Program extends Node {

    private enum Kind {
        PROGRAM, INTERFACE 
    }

    private Kind kind;
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

    public void accept(Visitor v) {
        v.visit(this);
    }
}
