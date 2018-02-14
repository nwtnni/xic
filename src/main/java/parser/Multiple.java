package parser;

import java.util.ArrayList;

public class Multiple extends Expression {
    public ArrayList<Node> values;

    public Multiple(ArrayList<Node> values) {
        this.values = values;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
