package parser;

import java.util.ArrayList;

public class Block extends Statement {
    
    public ArrayList<Node> statements;

    public Block(ArrayList<Node> statements) {
        this.statements = statements;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
