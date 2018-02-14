package parser;

public interface Visitor {

    /*
     * Top-level AST nodes
     */
    public void visit(Program p);

    public void visit(Use u);

    public void visit(Function f);

    /*
     * Statement nodes
     */
    public void visit(Declare d);

    public void visit(Assign a);

    public void visit(Return r);

    public void visit(Block b);

    public void visit(If i);

    public void visit(Else e);

    public void visit(While w);

    /*
     * Expression nodes
     */
    public void visit(Binary b);

    public void visit(Unary u);

    public void visit(Variable v);

    public void visit(Multiple m);

    public void visit(Index i);

    public void visit(XiInt i);

    public void visit(XiBool b);

    public void visit(XiChar c);

    public void visit(XiString s);

    public void visit(XiArray a);

    /*
     * Other nodes
     */

    public void visit(Call c);

    public void visit(Type t);
}
