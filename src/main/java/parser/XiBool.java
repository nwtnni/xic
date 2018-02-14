package parser;

public class XiBool extends Expression {

    public boolean value;

    public XiBool(boolean value) {
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
