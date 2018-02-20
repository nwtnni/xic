package ast;

/* Visits a constructed AST and checks all node invariants.
 * 
 * Note: must run with the -ea flag to enable assertions.
 */
public class Invariant implements Visitor<Void> {

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
    public Void visit(Program p) {
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
        return null;
    }

    // Use invariants:
    //
    // [u.location] non-null
    //
    // [u.file]     non-null
    //
    public Void visit(Use u) {
        assert u.location != null;
        assert u.file != null; 
        return null;
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
    public Void visit(Function f) {
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
        return null;
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
    public Void visit(Declare d) {
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
    public Void visit(Assign a) {
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
    public Void visit(Return r) {
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
    // [b.location] non-null
    //
    // [b.statements] non-null && length > 1
    //
    public Void visit(Block b) {
        assert b.location != null;
        assert b.statements != null && b.statements.size() > 0;
        for (Node statement : b.statements) {
            statement.accept(this);
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
    public Void visit(If i) {
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
    public Void visit(Else e) {
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
    public Void visit(While w) {
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
    public Void visit(Call c) {
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
    public Void visit(Binary b) {
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
    public Void visit(Unary u) {
        assert u.location != null;    
        assert u.child != null;
        u.child.accept(this);
        return null;
    }

    // Variable invariants:
    //
    // [v.location] non-null
    //
    // [v.id]       non-null
    //
    public Void visit(Variable v) {
        assert v.location != null;  
        assert v.id != null;  
        return null;
    }

    // Multiple invariants:
    //
    // [m.location] non-null
    //
    // [m.values]   non-null
    //
    public Void visit(Multiple m) {
        assert m.location != null; 
        assert m.values != null;

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
    public Void visit(Index i) {
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
    public Void visit(XiInt i) {
        assert i.location != null; 
        return null;
    }

    // XiBool invariants:
    //
    // [b.location] non-null
    //
    public Void visit(XiBool b) {
        assert b.location != null; 
        return null;
    }

    // XiChar invariants:
    //
    // [c.location] non-null
    //
    public Void visit(XiChar c) {
        assert c.location != null; 
        assert c.escaped != null;
        return null;
    }

    // XiString invariants:
    //
    // [s.location] non-null
    //
    public Void visit(XiString s) {
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
    public Void visit(XiArray a) {
        assert a.location != null; 
        assert a.values != null;

        for (Node value : a.values) {
            value.accept(this);
        }
        return null;
    }

    public Void visit(XiType t) {
        //TODO
        return null;
    }
}
