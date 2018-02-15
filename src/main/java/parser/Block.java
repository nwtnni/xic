package parser;

import java_cup.runtime.ComplexSymbolFactory.Location;
import java.util.ArrayList;

public class Block extends Statement {
    
    public ArrayList<Node> statements;

    public Block(Location location, ArrayList<Node> statements) {
        this.location = location;
        this.statements = statements;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
