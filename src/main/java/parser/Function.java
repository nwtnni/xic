package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Function extends Node {
    
    public String id;
    public ArrayList<Node> args;
    public ArrayList<Node> types;
    public Node block;

    public Function(Location location, String id, ArrayList<Node> args, ArrayList<Node> types, Node block) {
        this.location = location;
        this.id = id;  
        this.args = args;
        this.types = types;
        this.block = block;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
