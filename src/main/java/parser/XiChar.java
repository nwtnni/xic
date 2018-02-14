package parser;

public class XiChar extends Expression {

    public char value;

    public XiChar(char value) {
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
