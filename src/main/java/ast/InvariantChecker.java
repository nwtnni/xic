package ast;

import xic.XicException;
import xic.XicInternalException;

/* Visits a constructed AST and checks all node invariants.
 * 
 * Note: must run with the -ea flag to enable assertions.
 */
public class InvariantChecker extends ASTVisitor<Void> {

    public static final InvariantChecker CHECKER = new InvariantChecker();

    public static void check(Node ast) {
        try {
            ast.accept(CHECKER);
        } catch (Exception e) {
            e.printStackTrace();
            throw XicInternalException.runtime("violated ast invariant");
        }
    }

    /*
     * Top-level AST nodes
     */

    // Program invariants:
    //
    // [p.uses]      non-null
    //
    // [p.fns] non-null && list of TopDeclr nodes
    //
    public Void visit(XiProgram p) throws XicException {
        assert p.uses != null; 
        for (Node use : p.uses) {
            assert use instanceof XiUse;
            use.accept(this);
        }

        assert p.body != null;
        for (Node f : p.body) {
            assert f instanceof TopDeclr;
            f.accept(this);
        }
        return null;
    }

    // Use invariants:
    //
    // [u.location] non-null
    //
    // [u.file]     non-null
    //
    public Void visit(XiUse u) throws XicException {
        assert u.location != null;
        assert u.file != null; 
        return null;
    }

    // Fn invariants:
    //
    // [f.location] non-null
    //
    // [f.id]       non-null
    //
    // [f.args]     non-null
    //
    // [f.returns]  non-null
    //           
    // [f.block]    non-null && Block node if Kind.FN || Kind.PROC
    //              null otherwise
    //
    public Void visit(XiFn f) throws XicException {
        assert f.location != null;
    
        assert f.id != null;
        assert f.args != null;
        assert f.returns != null;
        
        for (Node arg : f.args) {
            arg.accept(this);
        }

        for (Node ret : f.returns) {
            ret.accept(this);
        }

        if (f.isDef()) {
            assert f.block instanceof XiBlock;
            f.block.accept(this);
        } else {
            assert f.block == null; 
        }
        return null;
    }

    // TODO: PA7
    public Void visit(XiClass c) throws XicException {
        return null;
    }
    
    // TODO: PA7
    public Void visit(XiGlobal g) throws XicException {
        return null;
    }

    /*
     * Statement nodes
     */

    // Assign invariants:
    //
    // [a.location] non-null
    //
    // [a.lhs]      non-null
    //
    // [a.rhs]      non-null
    //
    public Void visit(XiAssign a) throws XicException {
        assert a.location != null;
        assert a.lhs != null;
        assert a.rhs != null;
        for (Node n : a.lhs) {
            n.accept(this);
        }
        a.rhs.accept(this);
        return null;
    }

    // Block invariants:
    //
    // [b.location]   non-null
    //
    // [b.statements] non-null
    //
    public Void visit(XiBlock b) throws XicException {
        assert b.location != null;
        assert b.statements != null;

        for (Node statement : b.statements) {
            statement.accept(this);
        }

        return null;
    }

    // TODO: PA7
    public Void visit(XiBreak b) throws XicException {
        return null;
    }

    // Declare invariants:
    //
    // [d.location] non-null
    //
    // [d.id]       null if Kind.UNDERSCORE
    //              non-null && Var node otherwise
    //
    // [d.type]     null if Kind.UNDERSCORE
    //              non-null && XiType node otherwise
    //
    public Void visit(XiDeclr d) throws XicException {
        assert d.location != null;
        if (d.isUnderscore()) {
            assert d.id == null;
            assert d.xiType == null;
        } else {
            assert d.id != null;
            assert d.xiType instanceof XiType;
            d.xiType.accept(this);
        }
        return null;
    }

    // If invariants:
    //
    // [i.location]  non-null
    //
    // [i.guard]     non-null
    //
    // [i.block]     non-null && Block node or node from
    //               a SingleStmtNoReturn
    //
    // [i.elseBlock] non-null && Block node or node from
    //               a SingleStmtNoReturn if Kind.IF_ELSE
    //               null otherwise
    //
    public Void visit(XiIf i) throws XicException {
        assert i.location != null;
        assert i.guard != null; 
        i.guard.accept(this);

        Node block = i.block; 
        assert (block instanceof XiBlock ||
                block instanceof XiAssign ||
                block instanceof XiIf ||
                block instanceof XiWhile ||
                block instanceof XiCall ||
                block instanceof XiDeclr);
        i.guard.accept(this);

        if (i.hasElse()) {
            block = i.elseBlock; 
            assert (block instanceof XiBlock ||
                    block instanceof XiAssign ||
                    block instanceof XiIf ||
                    block instanceof XiWhile ||
                    block instanceof XiCall ||
                    block instanceof XiDeclr);
            i.elseBlock.accept(this);
        } else {
            assert i.elseBlock == null; 
        }
        return null;
    }

    // Return invariants:
    //
    // [r.location] non-null
    //
    // [r.value]    non-null if Kind.VALUE
    //              null otherwise
    //
    public Void visit(XiReturn r) throws XicException {
        assert r.location != null;
        if (r.hasValues()) {
            assert r.values != null; 
            for (Node n : r.values) {
                n.accept(this);
            }
        } else {
            assert r.values == null;
        }
        return null;
    }

    // While invariants:
    //
    // [i.location] non-null
    //
    // [i.guard]    non-null
    //
    // [i.block]    non-null && Block node or node from
    //              a SingleStmtNoReturn
    //
    public Void visit(XiWhile w) throws XicException {
        assert w.location != null;
        assert w.guard != null;
        Node block = w.block; 
        assert (block instanceof XiBlock ||
                block instanceof XiAssign ||
                block instanceof XiIf ||
                block instanceof XiWhile ||
                block instanceof XiCall ||
                block instanceof XiDeclr);
        return null;
    }

    /*
     * Expression nodes
     */

    // Binary invariants:
    //
    // [b.location] non-null
    //
    // [b.lhs]      non-null
    //
    // [b.rhs]      non-null
    //
    public Void visit(XiBinary b) throws XicException {
        assert b.location != null;
        assert b.lhs != null; 
        assert b.rhs != null;
        b.lhs.accept(this);
        b.rhs.accept(this);
        return null;
    }

    // Call invariants:
    //
    // [c.location] non-null
    //
    // [c.id]       non-null
    //
    // [c.args]     non-null
    //
    public Void visit(XiCall c) throws XicException {
        assert c.location != null;
        assert c.id != null; 
        assert c.args != null;
        for (Node n: c.args) {
            n.accept(this);
        }
        return null;
    }

    // TODO: PA7
    public Void visit(XiDot d) throws XicException {
        return null;
    }

    // TODO: PA7
    public Void visit(XiExprStmt e) throws XicException {
        return null;
    }

    // Index invariants:
    //
    // [i.location] non-null
    // 
    // [i.array]    non-null
    //
    // [i.index]    non-null
    //
    public Void visit(XiIndex i) throws XicException {
        assert i.location != null; 
        assert i.array != null; 
        assert i.index != null;

        i.array.accept(this);
        i.index.accept(this);
        return null;
    }

    // TODO: PA7
    public Void visit(XiNew n) throws XicException {
        return null;
    }

    // TODO: PA7
    public Void visit(XiThis u) throws XicException {
        return null;
    }

    // Unary invariants:
    //
    // [u.location] non-null
    //
    // [u.child]    non-null
    //
    public Void visit(XiUnary u) throws XicException {
        assert u.location != null;    
        assert u.child != null;
        u.child.accept(this);
        return null;
    }

    // Var invariants:
    //
    // [v.location] non-null
    //
    // [v.id]       non-null
    //
    public Void visit(XiVar v) throws XicException {
        assert v.location != null;  
        assert v.id != null;  
        return null;
    }

    /*
     * Constant nodes
     */

    // XiArray invariants:
    //
    // [a.location] non-null
    //
    // [a.values]   non-null
    //
    public Void visit(XiArray a) throws XicException {
        assert a.location != null; 
        assert a.values != null;

        for (Node value : a.values) {
            value.accept(this);
        }
        return null;
    }

    // XiBool invariants:
    //
    // [b.location] non-null
    //
    public Void visit(XiBool b) throws XicException {
        assert b.location != null; 
        return null;
    }

    // XiChar invariants:
    //
    // [c.location] non-null
    //
    public Void visit(XiChar c) throws XicException {
        assert c.location != null; 
        assert c.escaped != null;
        return null;
    }

    // XiInt invariants:
    //
    // [i.location] non-null
    //
    public Void visit(XiInt i) throws XicException {
        assert i.location != null; 
        return null;
    }

    // XiNull invariants:
    //
    // [c.location] non-null
    //
    public Void visit(XiNull n) throws XicException {
        assert n.location != null;
        return null;
    }

    // XiString invariants:
    //
    // [s.location] non-null
    //
    public Void visit(XiString s) throws XicException {
        assert s.location != null; 
        assert s.escaped != null;
        assert s.value != null;
        return null;
    }

    // XiType invariants:
    //
    // [t.location] non-null
    //
    // [t.size]     possibly non-null if Kind.ARRAY
    //              null otherwise
    //
    // [t.child]    non-null && XiType if Kind.Array
    //              null otherwise
    //
    // [t.id]       non-null if Kind.CLASS
    //              null otherwise
    //
    public Void visit(XiType t) throws XicException {
        assert t.location != null;  
        
        if (t.isClass()) {
            assert t.size == null; 
            assert t.child == null; 
            assert t.id != null;
        } else {
            assert t.child instanceof XiType;
            assert t.id == null;
        }
        return null;
    }
}
