package parser;

public class Return extends Statement {

    public Node value;

    public Return(Node value) {
        this.value = value;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
