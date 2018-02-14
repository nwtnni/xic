package parser;

import java.util.ArrayList;

public class Program extends Node {
    public ArrayList<Node> uses;
    public ArrayList<Node> functions;

    public void accept(Visitor v) {
        v.visit(this);
    }
}
