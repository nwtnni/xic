package emit;

import ast.*;
import ir.*;
import xic.XicException;

public class Emitter extends Visitor<IRNode> {

    /*
     * Top-level AST nodes
     */
    public IRNode visit(Program p) throws XicException {
        return null;
    }

    public IRNode visit(Use u) throws XicException {
        return null;
    }

    public IRNode visit(Fn f) throws XicException {
        return null;
    }

    /*
     * Statement nodes
     */
    public IRNode visit(Declare d) throws XicException {
        return null;
    }

    public IRNode visit(Assign a) throws XicException {
        return null;
    }

    public IRNode visit(Return r) throws XicException {
        return null;
    }

    public IRNode visit(Block b) throws XicException {
        return null;
    }

    public IRNode visit(If i) throws XicException {
        return null;
    }

    public IRNode visit(While w) throws XicException {
        return null;
    }

    /*
     * Expression nodes
     */
    public IRNode visit(Call c) throws XicException {
        return null;
    }

    public IRNode visit(Binary b) throws XicException {
        return null;
    }

    public IRNode visit(Unary u) throws XicException {
        return null;
    }

    public IRNode visit(Var v) throws XicException {
        return null;
    }

    public IRNode visit(Multiple m) throws XicException {
        return null;
    }

    public IRNode visit(Index i) throws XicException {
        return null;
    }

    public IRNode visit(XiInt i) throws XicException {
        return null;
    }

    public IRNode visit(XiBool b) throws XicException {
        return null;
    }

    public IRNode visit(XiChar c) throws XicException {
        return null;
    }

    public IRNode visit(XiString s) throws XicException {
        return null;
    }

    public IRNode visit(XiArray a) throws XicException {
        return null;
    }

    /*
     * Other nodes
     */
    @Override
    public IRNode visit(XiType t) throws XicException {
        return null;
    }
}