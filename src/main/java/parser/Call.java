package parser;

import java.util.ArrayList;

public class Call extends Expression {
    
    public Node id;
    public ArrayList<Node> args; 
    
    public Call(Node id, ArrayList<Node> args) {
        this.id = id; 
        this.args = args;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
