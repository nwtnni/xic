package ast;

import java.util.List;
import java.util.ArrayList;

import xic.XicException;

// Boilerplate code to implement the visitor pattern
public abstract class ASTVisitor<T> {

    /*
     * Psuedo-visit method for visiting a list of nodes.
     */
    public List<T> visit(List<Node> nodes) throws XicException {
        List<T> t = new ArrayList<>();
        for (Node n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }

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

    public T visit(Assign a) throws XicException {
        return null;
    }

    public T visit(Block b) throws XicException {
        return null;
    }

    public T visit(Declare d) throws XicException {
        return null;
    }

    public T visit(If i) throws XicException {
        return null;
    }

    public T visit(Return r) throws XicException {
        return null;
    }

    public T visit(While w) throws XicException {
        return null;
    }

    /*
     * Expression nodes
     */

    public T visit(Binary b) throws XicException {
        return null;
    }

    public T visit(Call c) throws XicException {
        return null;
    }

    public T visit(Index i) throws XicException {
        return null;
    }

    public T visit(Unary u) throws XicException {
        return null;
    }

    public T visit(Var v) throws XicException {
        return null;
    }

    /*
     * Constant nodes
     */

    public T visit(XiArray a) throws XicException {
        return null;
    }

    public T visit(XiBool b) throws XicException {
        return null;
    }

    public T visit(XiChar c) throws XicException {
        return null;
    }

    public T visit(XiInt i) throws XicException {
        return null;
    }

    public T visit(XiString s) throws XicException {
        return null;
    }

    public T visit(XiType t) throws XicException {
        return null;
    }
}
