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

    public void visit(Index i);

    public void visit(Assign a);

    public void visit(Return r);

    public void visit(Call c);

    public void visit(Block b);

    public void visit(If i);

    public void visit(Else e);

    public void visit(While w);
}
