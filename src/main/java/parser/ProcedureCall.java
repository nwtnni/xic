package parser;

public class ProcedureCall extends Statement {

    public Node id;

    public ProcedureCall(Node id) {
        this.id = id;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
