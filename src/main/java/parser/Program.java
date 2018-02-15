package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Program extends Node {
    public ArrayList<Node> uses;
    public ArrayList<Node> functions;

    public Program(Location location, ArrayList<Node> uses, ArrayList<Node> functions) {
        this.location = location;
        this.uses = uses;
        this.functions = functions;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
