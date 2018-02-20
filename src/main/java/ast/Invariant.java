package ast;

/* Visits a constructed AST and checks all node invariants.
 * 
 * Note: must run with the -ea flag to enable assertions.
 */
public class Invariant implements Visitor {

    public static final Invariant CHECKER = new Invariant();

    public static void check(Node ast) {
        ast.accept(CHECKER);
    }

    // Program invariants:
    //
    // [p.uses]      non-null for .xi file && Use nodes
    //               null for .ixi
    //
    // [p.functions] non-null && list of Function nodes
    //
    public void visit(Program p) {
        if (p.isProgram()) {
            assert p.uses != null; 
            for (Node use : p.uses) {
                assert use instanceof Use;
                use.accept(this);
            }
        } else {
            assert p.uses == null; 
        }
        assert p.functions != null;
        for (Node function : p.functions) {
            assert function instanceof Function;
            function.accept(this);
        }
    }

    // Use invariants:
    //
    // [u.location] non-null
    //
    // [u.file]     non-null
    //
    public void visit(Use u) {
        assert u.location != null;
        assert u.file != null; 
    }

    // Function invariants:
    //
    // [f.location] non-null
    //
    // [f.id]       non-null
    //
    // [f.args]     non-null && Declare nodes
    //
    // [f.types]    non-null && XiType nodes if Kind.FUNCTION || Kind.FUNCTION_HEADER
    //              null otherwise
    //
    // [f.block]    non-null && Block node if Kind.FUNCTION || Kind.PROCEDURE
    //              null otherwise
    //
    public void visit(Function f) {
        assert f.location != null;
    
        assert f.id != null;
        assert f.args != null;

        for (Node arg : f.args) {
            assert arg instanceof Declare;
            arg.accept(this);
        }

        if (f.isFunction()) {
            assert f.types != null;
            for (Node type : f.types) {
                assert type instanceof XiType; 
                type.accept(this);
            }
        } else {
            assert f.types == null;
        }

        if (f.isDefinition()) {
            assert f.block instanceof Block;
            f.block.accept(this);
        } else {
            assert f.block == null; 
        }
    }

    // Declare invariants:
    //
    // [d.location] non-null
    //
    // [d.id]       null if Kind.UNDERSCORE
    //              non-null && Variable node otherwise
    //
    // [d.type]     null if Kind.UNDERSCORE
    //              non-null && XiType node otherwise
    //
    public void visit(Declare d) {
        assert d.location != null;
        if (d.isUnderscore()) {
            assert d.id == null;
            assert d.type == null;
        } else {
            assert d.id instanceof Variable;
            assert d.type instanceof XiType;
            d.id.accept(this);
            d.type.accept(this);
        }
    }

    // Assign invariants:
    //
    // [a.location] non-null
    //
    // [a.lhs]      non-null
    //
    // [a.rhs]      non-null
    //
    public void visit(Assign a) {
        assert a.location != null;
        assert a.lhs != null;   
        assert a.rhs != null;   
        a.lhs.accept(this);
        a.rhs.accept(this);
    }

    // Return invariants:
    //
    // [r.location] non-null
    //
    // [r.value]    non-null if Kind.VALUE
    //              null otherwise
    //
    public void visit(Return r) {
        assert r.location != null;
        if (r.hasValue()) {
            assert r.value != null; 
            r.value.accept(this);
        } else {
            assert r.value == null;
        }
    }

    // Block invariants:
    //
    // [b.location] non-null
    //
    // [b.statements] non-null && length > 1
    //
    public void visit(Block b) {
        assert b.location != null;
        assert b.statements != null && b.statements.size() > 0;
        for (Node statement : b.statements) {
            statement.accept(this);
        }
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
    public void visit(If i) {
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
    }

    // Else invariants:
    //
    // [e.location] non-null
    //
    // [e.block]    non-null && Block node
    //
    public void visit(Else e) {
        assert e.location != null;
        assert e.block instanceof Block;
        e.block.accept(this);
    }

    // While invariants:
    //
    // [i.location] non-null
    //
    // [i.guard]    non-null
    //
    // [i.block]    non-null && Block node
    //
    public void visit(While w) {
        assert w.location != null;
        assert w.guard != null; 
        assert w.block instanceof Block;
    }

    // Call invariants:
    //
    // [c.location] non-null
    //
    // [c.id]       non-null
    //
    // [c.args]     non-null
    //
    public void visit(Call c) {
        assert c.location != null;
        assert c.id != null; 
        assert c.args != null;

        for (Node arg : c.args) {
            arg.accept(this);
        }
    }

    // Binary invariants:
    //
    // [b.location] non-null
    //
    // [b.lhs]      non-null
    //
    // [b.rhs]      non-null
    //
    public void visit(Binary b) {
        assert b.location != null;
        assert b.lhs != null; 
        assert b.rhs != null;
        b.lhs.accept(this);
        b.rhs.accept(this);
    }

    // Unary invariants:
    //
    // [u.location] non-null
    //
    // [u.child]    non-null
    //
    public void visit(Unary u) {
        assert u.location != null;    
        assert u.child != null;
        u.child.accept(this);
    }

    // Variable invariants:
    //
    // [v.location] non-null
    //
    // [v.id]       non-null
    //
    public void visit(Variable v) {
        assert v.location != null;  
        assert v.id != null;  
    }

    // Multiple invariants:
    //
    // [m.location] non-null
    //
    // [m.values]   non-null
    //
    public void visit(Multiple m) {
        assert m.location != null; 
        assert m.values != null;

        for (Node value : m.values) {
            value.accept(this);
        }
    }

    // Index invariants:
    //
    // [i.location] non-null
    // 
    // [i.array]    non-null
    //
    // [i.index]    non-null
    //
    public void visit(Index i) {
        assert i.location != null; 
        assert i.array != null; 
        assert i.index != null;

        i.array.accept(this);
        i.index.accept(this);
    }

    // XiInt invariants:
    //
    // [i.location] non-null
    //
    public void visit(XiInt i) {
        assert i.location != null; 
    }

    // XiBool invariants:
    //
    // [b.location] non-null
    //
    public void visit(XiBool b) {
        assert b.location != null; 
    }

    // XiChar invariants:
    //
    // [c.location] non-null
    //
    public void visit(XiChar c) {
        assert c.location != null; 
        assert c.escaped != null;
    }

    // XiString invariants:
    //
    // [s.location] non-null
    //
    public void visit(XiString s) {
        assert s.location != null; 
        assert s.escaped != null;
        assert s.value != null;
    }

    // XiArray invariants:
    //
    // [a.location] non-null
    //
    // [a.values]   non-null
    //
    public void visit(XiArray a) {
        assert a.location != null; 
        assert a.values != null;

        for (Node value : a.values) {
            value.accept(this);
        }
    }

    public void visit(XiType t) {
        //TODO
        return;
    }
}
