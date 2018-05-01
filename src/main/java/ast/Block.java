package ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;


// A Block of statements
public class Block extends Node {

    public List<Node> statements;

    public Block(Location location, List<Node> statements) {
        this.location = location;
        this.statements = statements;
    }

    public Block(Location location, Node... statements) {
        this.location = location;
        this.statements = new ArrayList<>(Arrays.asList(statements));
    }
    
    public Block(List<Node> statements) {
        this.statements = statements;
    }

    public <T> T accept(Visitor<T> v) throws XicException {
        return v.visit(this);
    }
}
