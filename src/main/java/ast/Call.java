package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class Call extends Node {
    
    public Node id;
    public ArrayList<Node> args; 
    
    public Call(Location location, Node id, ArrayList<Node> args) {
        this.location = location;
        this.id = id; 
        this.args = args;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
