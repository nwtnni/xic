package ast;

import xic.XicException;

/* Visits a constructed AST and checks all node invariants.
 * 
 * Note: must run with the -ea flag to enable assertions.
 */
public class Invariant extends Visitor<Void> {

    public static final Invariant CHECKER = new Invariant();

    public static void check(Node ast) {
        try {
			ast.accept(CHECKER);
		} catch (Exception e) {
			// TODO what should we do here?
			e.printStackTrace();
		}
    }

    // Program invariants:
    //
    // [p.uses]      non-null for .xi file && Use nodes
    //               null for .ixi
    //
    // [p.fns] non-null && list of Fn nodes
    //
    public Void visit(Program p) throws XicException {
        if (p.isProgram()) {
            assert p.uses != null; 
            for (Node use : p.uses) {
                assert use instanceof Use;
                use.accept(this);
            }
        } else {
            assert p.uses == null; 
        }
        assert p.fns != null;
        for (Node f : p.fns) {
            assert f instanceof Fn;
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
    public Void visit(Use u) throws XicException {
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
    public Void visit(Fn f) throws XicException {
        assert f.location != null;
    
        assert f.id != null;
        assert f.args != null;
        assert f.returns != null;
        
        f.args.accept(this);
        f.returns.accept(this);

        if (f.isDef()) {
            assert f.block instanceof Block;
            f.block.accept(this);
        } else {
            assert f.block == null; 
        }
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
    public Void visit(Declare d) throws XicException {
        assert d.location != null;
        if (d.isUnderscore()) {
            assert d.id == null;
            assert d.type == null;
        } else {
        	assert d.id != null;
            assert d.type instanceof XiType;
            d.type.accept(this);
        }
        return null;
    }

    // Assign invariants:
    //
    // [a.location] non-null
    //
    // [a.lhs]      non-null
    //
    // [a.rhs]      non-null
    //
    public Void visit(Assign a) throws XicException {
        assert a.location != null;
        assert a.lhs != null;   
        assert a.rhs != null;   
        a.lhs.accept(this);
        a.rhs.accept(this);
        return null;
    }

    // Return invariants:
    //
    // [r.location] non-null
    //
    // [r.value]    non-null if Kind.VALUE
    //              null otherwise
    //
    public Void visit(Return r) throws XicException {
        assert r.location != null;
        if (r.hasValue()) {
            assert r.value != null; 
            r.value.accept(this);
        } else {
            assert r.value == null;
        }
        return null;
    }

    // Block invariants:
    //
    // [b.location]   non-null
    //
    // [b.statements] non-null
    //
    // [b.return]     non-null if Kind.RETURN
    //                null otherwise
    //
    public Void visit(Block b) throws XicException {
        assert b.location != null;
        assert b.statements != null;
        for (Node statement : b.statements) {
            statement.accept(this);
        }

        if (b.hasReturn()) {
            assert b.returns != null; 
            b.returns.accept(this);
        }
        return null;
    }

    // If invariants:
    //
    // [i.location]  non-null
    //
    // [i.guard]     non-null
    //
    // [i.block]     non-null && Block node
    //
    // [i.elseBlock] non-null && Block node if Kind.IF_ELSE
    //               null otherwise
    //
    public Void visit(If i) throws XicException {
        assert i.location != null;
        assert i.guard != null; 
        i.guard.accept(this);

        assert i.block instanceof Block; 
        i.guard.accept(this);

        if (i.hasElse()) {
            assert i.elseBlock instanceof Else; 
            i.elseBlock.accept(this);
        } else {
            assert i.elseBlock == null; 
        }
        return null;
    }

    // Else invariants:
    //
    // [e.location] non-null
    //
    // [e.block]    non-null && Block node
    //
    public Void visit(Else e) throws XicException {
        assert e.location != null;
        assert e.block instanceof Block;
        e.block.accept(this);
        return null;
    }

    // While invariants:
    //
    // [i.location] non-null
    //
    // [i.guard]    non-null
    //
    // [i.block]    non-null && Block node
    //
    public Void visit(While w) throws XicException {
        assert w.location != null;
        assert w.guard != null; 
        assert w.block instanceof Block;
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
    public Void visit(Call c) throws XicException {
        assert c.location != null;
        assert c.id != null; 
        assert c.args != null;

        for (Node arg : c.args) {
            arg.accept(this);
        }
        return null;
    }

    // Binary invariants:
    //
    // [b.location] non-null
    //
    // [b.lhs]      non-null
    //
    // [b.rhs]      non-null
    //
    public Void visit(Binary b) throws XicException {
        assert b.location != null;
        assert b.lhs != null; 
        assert b.rhs != null;
        b.lhs.accept(this);
        b.rhs.accept(this);
        return null;
    }

    // Unary invariants:
    //
    // [u.location] non-null
    //
    // [u.child]    non-null
    //
    public Void visit(Unary u) throws XicException {
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
    public Void visit(Var v) throws XicException {
        assert v.location != null;  
        assert v.id != null;  
        return null;
    }

    // Multiple invariants:
    //
    // [m.location] non-null
    //
    // [m.values]   non-null && size != 1
    //
    public Void visit(Multiple m) throws XicException {
        assert m.location != null; 
        assert m.values != null;
        assert m.values.size() != 1;

        for (Node value : m.values) {
            value.accept(this);
        }
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
    public Void visit(Index i) throws XicException {
        assert i.location != null; 
        assert i.array != null; 
        assert i.index != null;

        i.array.accept(this);
        i.index.accept(this);
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
