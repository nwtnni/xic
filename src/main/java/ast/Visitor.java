package ast;

import xic.XicException;

public abstract class Visitor<T> {

    /*
     * Top-level AST nodes
     */
    public T visit(Program p) throws XicException {
        return null;
    }

    public T visit(Use u) throws XicException {
        return null;
    }

    public T visit(Fn f) throws XicException {
        return null;
    }

    /*
     * Statement nodes
     */
    public T visit(Declare d) throws XicException {
        return null;
    }

    public T visit(Assign a) throws XicException {
        return null;
    }

    public T visit(Return r) throws XicException {
        return null;
    }

    public T visit(Block b) throws XicException {
        return null;
    }

    public T visit(If i) throws XicException {
        return null;
    }

    public T visit(Else e) throws XicException {
        return null;
    }

    public T visit(While w) throws XicException {
        return null;
    }

    /*
     * Expression nodes
     */
    public T visit(Call c) throws XicException {
        return null;
    }

    public T visit(Binary b) throws XicException {
        return null;
    }

    public T visit(Unary u) throws XicException {
        return null;
    }

    public T visit(Var v) throws XicException {
        return null;
    }

    public T visit(Multiple m) throws XicException {
        return null;
    }

    public T visit(Index i) throws XicException {
        return null;
    }

    public T visit(XiInt i) throws XicException {
        return null;
    }

    public T visit(XiBool b) throws XicException {
        return null;
    }

    public T visit(XiChar c) throws XicException {
        return null;
    }

    public T visit(XiString s) throws XicException {
        return null;
    }

    public T visit(XiArray a) throws XicException {
        return null;
    }

    /*
     * Other nodes
     */
    public T visit(XiType t) throws XicException {
        return null;
    }
}
