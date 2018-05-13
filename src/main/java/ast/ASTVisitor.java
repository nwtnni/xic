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
    
    public T visit(XiProgram p) throws XicException {
        return null;
    }

    public T visit(XiUse u) throws XicException {
        return null;
    }

    public T visit(XiClass c) throws XicException {
        return null;
    }

    public T visit(XiFn f) throws XicException {
        return null;
    }

    public T visit(XiGlobal g) throws XicException {
        return null;
    }

    /*
     * Statement nodes
     */

    public T visit(XiAssign a) throws XicException {
        return null;
    }

    public T visit(XiBlock b) throws XicException {
        return null;
    }

    public T visit(XiBreak b) throws XicException {
        return null;
    }

    public T visit(XiDeclr d) throws XicException {
        return null;
    }

    public T visit(XiIf i) throws XicException {
        return null;
    }

    public T visit(XiReturn r) throws XicException {
        return null;
    }

    public T visit(XiWhile w) throws XicException {
        return null;
    }

    /*
     * Expression nodes
     */

    public T visit(XiBinary b) throws XicException {
        return null;
    }

    public T visit(XiCall c) throws XicException {
        return null;
    }

    public T visit(XiDot d) throws XicException {
        return null;
    }

    public T visit(XiExprStmt e) throws XicException {
        return null;
    }

    public T visit(XiIndex i) throws XicException {
        return null;
    }

    public T visit(XiNew n) throws XicException {
        return null;
    }

    public T visit(XiThis u) throws XicException {
        return null;
    }

    public T visit(XiUnary u) throws XicException {
        return null;
    }

    public T visit(XiVar v) throws XicException {
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

    public T visit(XiNull n) throws XicException {
        return null;
    }

    public T visit(XiString s) throws XicException {
        return null;
    }

    public T visit(XiType t) throws XicException {
        return null;
    }
}
