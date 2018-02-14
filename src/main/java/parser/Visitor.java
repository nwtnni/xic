package parser;

public interface Visitor {

    public void visit(Binary b);

    public void visit(Unary u);

    public void visit(Value v);

    public void visit(Declare d);

    public void visit(Function f);

    public void visit(Program p);

    public void visit(Use u);

    public void visit(Variable v);

    public void visit(Multiple m);

    public void visit(ArrayIndex ai);
}
