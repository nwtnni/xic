package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;

public class FunctionCall extends Expression {
    
    public Node id;
    public ArrayList<Node> args; 
    
    public FunctionCall(Location location, Node id, ArrayList<Node> args) {
        this.location = location;
        this.id = id; 
        this.args = args;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
