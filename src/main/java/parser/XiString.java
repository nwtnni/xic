package parser;

public class XiString extends Expression {

    public String value;

    public XiString(String value) {
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
