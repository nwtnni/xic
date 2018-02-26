package ast;

import xic.XicException;

import java.util.ArrayList;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Block extends Node {

    public ArrayList<Node> statements;

    public Block(Location location, ArrayList<Node> statements) {
        this.location = location;
    	this.statements = statements;
    }
    
    public Block(ArrayList<Node> statements) {
        this.statements = statements;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
