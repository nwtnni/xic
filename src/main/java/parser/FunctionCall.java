package parser;

import java.util.ArrayList;

public class FunctionCall extends Expression {
    
    public Node id;
    public ArrayList<Node> args; 
    
    public FunctionCall(Node id, ArrayList<Node> args) {
        this.id = id; 
        this.args = args;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
