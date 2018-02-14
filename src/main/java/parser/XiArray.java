package parser;

import java.util.ArrayList;

public class XiArray extends Expression {

    public ArrayList<Node> values;

    public XiArray(ArrayList<Node> values) {
        this.values = values;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
