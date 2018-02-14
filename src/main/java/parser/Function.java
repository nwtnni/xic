package parser;

import java.util.ArrayList;

public class Function extends Node {
    
    private String id;
    private ArrayList<Node> args;
    private ArrayList<Node> types;
    private Node block;

    public Function(String id, ArrayList<Node> args, ArrayList<Node> types, Node block) {
        this.id = id;  
        this.args = args;
        this.types = types;
        this.block = block;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
