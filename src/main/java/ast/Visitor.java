package ast;

public abstract class Visitor<T> {

    /*
     * Top-level AST nodes
     */
    public T visit(Program p) {
        return null;
    }

    public T visit(Use u) {
        return null;
    }

    public T visit(Fn f) {
        return null;
    }

    /*
     * Statement nodes
     */
    public T visit(Declare d) {
        return null;
    }

    public T visit(Assign a) {
        return null;
    }

    public T visit(Return r) {
        return null;
    }

    public T visit(Block b) {
        return null;
    }

    public T visit(If i) {
        return null;
    }

    public T visit(Else e) {
        return null;
    }

    public T visit(While w) {
        return null;
    }

    /*
     * Expression nodes
     */
    public T visit(Call c) {
        return null;
    }

    public T visit(Binary b) {
        return null;
    }

    public T visit(Unary u) {
        return null;
    }

    public T visit(Variable v) {
        return null;
    }

    public T visit(Multiple m) {
        return null;
    }

    public T visit(Index i) {
        return null;
    }

    public T visit(XiInt i) {
        return null;
    }

    public T visit(XiBool b) {
        return null;
    }

    public T visit(XiChar c) {
        return null;
    }

    public T visit(XiString s) {
        return null;
    }

    public T visit(XiArray a) {
        return null;
    }

    /*
     * Other nodes
     */
    public T visit(XiType t) {
        return null;
    }
}
