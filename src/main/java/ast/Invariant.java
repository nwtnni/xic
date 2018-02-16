package ast;

public class Invariant implements Visitor {

    public static final Invariant CHECKER = new Invariant();

    public static void check(Node ast) {
        ast.accept(CHECKER);
    }

    /*
     * Top-level AST nodes
     */
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

    public void visit(Use u) {
        assert u.location != null;
        assert u.file != null; 
    }

    public void visit(Function f) {
        assert f.location != null;

        boolean is_definition = f.isDefinition(); 
        boolean is_function = f.isFunction();
    
        assert f.id != null;
        assert f.args != null;

        for (Node arg : f.args) {
            assert arg instanceof Declare;
            arg.accept(this);
        }

        if (is_definition) {
            assert f.block instanceof Block;
            f.block.accept(this);
        } else {
            assert f.block == null; 
        }

        if (is_function) {
            assert f.types != null;
            for (Node type : f.types) {
                assert type instanceof Type; 
                type.accept(this);
            }
        } else {
            assert f.types == null;
        }
    }

    /*
     * Statement nodes
     */
    public void visit(Declare d) {
        assert d.location != null;
        if (d.isUnderscore()) {
            assert d.id == null;
            assert d.type == null;
        } else {
            assert d.id instanceof Variable;
            assert d.type instanceof Type;
            d.id.accept(this);
            d.type.accept(this);
        }
    }

    public void visit(Assign a) {
        assert a.location != null;
        assert a.lhs != null;   
        assert a.rhs != null;   
        a.lhs.accept(this);
        a.rhs.accept(this);
    }

    public void visit(Return r) {
        assert r.location != null;
        if (r.hasValue()) {
            assert r.value != null; 
            r.value.accept(this);
        } else {
            assert r.value == null;
        }
    }

    public void visit(Block b) {
        assert b.location != null;
        assert b.statements != null; 
        for (Node statement : b.statements) {
            statement.accept(this);
        }
    }

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

    public void visit(Else e) {
        assert e.location != null;
        assert e.block instanceof Block;
        e.block.accept(this);
    }

    public void visit(While w) {
        assert w.location != null;
        assert w.guard != null; 
        assert w.block instanceof Block;
    }

    /*
     * Expression nodes
     */
    public void visit(Call c) {
        assert c.location != null;
        assert c.id != null; 
        assert c.args != null;

        for (Node arg : c.args) {
            arg.accept(this);
        }
    }

    public void visit(Binary b) {
        assert b.location != null;
        assert b.lhs != null; 
        assert b.rhs != null;
        b.lhs.accept(this);
        b.rhs.accept(this);
    }

    public void visit(Unary u) {
        assert u.location != null;    
        assert u.child != null;
        u.child.accept(this);
    }

    public void visit(Variable v) {
        assert v.location != null;  
        assert v.id != null;  
    }

    public void visit(Multiple m) {
        assert m.location != null; 
        assert m.values != null;

        for (Node value : m.values) {
            value.accept(this);
        }
    }

    public void visit(Index i) {
        assert i.location != null; 
        assert i.array != null; 
        assert i.index != null;

        i.array.accept(this);
        i.index.accept(this);
    }

    public void visit(XiInt i) {
        assert i.location != null; 
    }

    public void visit(XiBool b) {
        assert b.location != null; 
    }

    public void visit(XiChar c) {
        assert c.location != null; 
        assert c.escaped != null;
    }

    public void visit(XiString s) {
        assert s.location != null; 
        assert s.escaped != null;
        assert s.value != null;
    }

    public void visit(XiArray a) {
        assert a.location != null; 
        assert a.values != null;

        for (Node value : a.values) {
            value.accept(this);
        }
    }

    /*
     * Other nodes
     */

    public void visit(Type t) {
        //TODO
        return;
    }
}
