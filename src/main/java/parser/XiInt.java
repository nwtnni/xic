package parser;

public class XiInt extends Expression {

    public long value;

    public XiInt(long value) {
        this.value = value; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
