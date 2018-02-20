package ast;

public interface Visitor<T> {

    /*
     * Top-level AST nodes
     */
    public T visit(Program p);

    public T visit(Use u);

    public T visit(Function f);

    /*
     * Statement nodes
     */
    public T visit(Declare d);

    public T visit(Assign a);

    public T visit(Return r);

    public T visit(Block b);

    public T visit(If i);

    public T visit(Else e);

    public T visit(While w);

    /*
     * Expression nodes
     */
    public T visit(Call c);

    public T visit(Binary b);

    public T visit(Unary u);

    public T visit(Variable v);

    public T visit(Multiple m);

    public T visit(Index i);

    public T visit(XiInt i);

    public T visit(XiBool b);

    public T visit(XiChar c);

    public T visit(XiString s);

    public T visit(XiArray a);

    /*
     * Other nodes
     */

    public T visit(XiType t);
}
