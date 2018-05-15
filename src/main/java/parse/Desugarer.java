package parse;

import java.util.List;
import java.util.ArrayList;

import ast.*;
import xic.XicException;

/** Removes syntactic sugar from AST. */
public class Desugarer extends ASTVisitor<Node> {

    /*
     * Psuedo-visit method for visiting a list of nodes.
     */
    public List<Node> visit(List<Node> nodes) throws XicException {
        List<Node> t = new ArrayList<>();
        for (Node n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }

    /*
     * Top-level AST nodes
     */
    
    public Node visit(XiProgram p) throws XicException {
        return null;
    }

    public Node visit(XiUse u) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiClass c) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiFn f) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiGlobal g) throws XicException {
        return null;
    }

    /*
     * Statement nodes
     */

    public Node visit(XiAssign a) throws XicException {
        return null;
    }

    public Node visit(XiBlock b) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiBreak b) throws XicException {
        return null;
    }

    public Node visit(XiDeclr d) throws XicException {
        return null;
    }

    public Node visit(XiIf i) throws XicException {
        return null;
    }

    public Node visit(XiReturn r) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiSeq s) throws XicException {
        return null;
    }

    public Node visit(XiWhile w) throws XicException {
        return null;
    }

    /*
     * Expression nodes
     */

    public Node visit(XiBinary b) throws XicException {
        return null;
    }

    public Node visit(XiCall c) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiDot d) throws XicException {
        return null;
    }

    public Node visit(XiIndex i) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiNew n) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiThis t) throws XicException {
        return null;
    }

    public Node visit(XiUnary u) throws XicException {
        return null;
    }

    public Node visit(XiVar v) throws XicException {
        return null;
    }

    /*
     * Constant nodes
     */

    public Node visit(XiArray a) throws XicException {
        return null;
    }

    public Node visit(XiBool b) throws XicException {
        return null;
    }

    public Node visit(XiChar c) throws XicException {
        return null;
    }

    public Node visit(XiInt i) throws XicException {
        return null;
    }

    // PA7
    public Node visit(XiNull n) throws XicException {
        return null;
    }

    public Node visit(XiString s) throws XicException {
        return null;
    }

    public Node visit(XiType t) throws XicException {
        return null;
    }
}
