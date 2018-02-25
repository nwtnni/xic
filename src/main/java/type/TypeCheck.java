package type;

import ast.*;
import xic.XicException;

public class TypeCheck extends Visitor<Type> {

    private static final TypeCheck CHECKER = new TypeCheck();

    private static TypeContext types;
    private static FnContext fns;
    private static VarContext vars;

    public static void check(Node ast) throws XicException {
        fns = FnContextFactory.from(ast);
        types = new TypeContext();
        vars = new VarContext();
        ast.accept(CHECKER);
    }

    /*
     * Top-level AST nodes
     */
    public Type visit(Program p) throws XicException {
        //TODO
        return null;
    }

    public Type visit(Use u) throws XicException {
        //TODO
        return null;
    }

    public Type visit(Fn f) throws XicException {
        //TODO
        return null;
    }

    /*
     * Statement nodes
     */
    public Type visit(Declare d) throws XicException {
        //TODO
        return null;
    }

    public Type visit(Assign a) throws XicException {
        //TODO
        return null;
    }

    public Type visit(Return r) throws XicException {
        //TODO
        return null;
    }

    public Type visit(Block b) throws XicException {
        //TODO
        return null;
    }

    public Type visit(If i) throws XicException {
        //TODO
        return null;
    }

    public Type visit(Else e) throws XicException {
        //TODO
        return null;
    }

    public Type visit(While w) throws XicException {
        //TODO
        return null;
    }

    /*
     * Expression nodes
     */

    // TODO
    // Context dependent
    public Type visit(Call c) throws XicException {
        assert false;
        return null;
    }

    public Type visit(Binary b) throws XicException {
        Type lt = b.lhs.accept(this);
        Type rt = b.rhs.accept(this);

        if (!lt.equals(rt)) { throw new RuntimeException("Mismatched types"); }
        
        if (lt.equals(Type.INT) && b.isInt()) {
            if (b.returnsBool()) {
                return Type.BOOL; 
            } else if (b.returnsInt()) {
                return Type.INT; 
            } else {
                throw new RuntimeException("Invalid integer operation");
            }
        }

        if (lt.equals(Type.BOOL) && b.isBool()) {
            return Type.BOOL; 
        }

        if (!lt.isClass() && b.isList()) {
            return lt;
        }

        throw new RuntimeException("Invalid binary operation for these types");
    }

    public Type visit(Unary u) throws XicException {
        Type ut = u.child.accept(this);
        if (u.isLogical()) {
            if (ut.equals(Type.BOOL)) {
                return Type.BOOL; 
            } else {
                throw new RuntimeException("Expected boolean for logical negation");
            }
        } else {
            if (ut.equals(Type.INT)) {
                return Type.INT;
            } else {
                throw new RuntimeException("Expected int for int negation");
            }
        }
    }

    // TODO
    // Context dependent
    public Type visit(Var v) throws XicException {
        assert false;
        return null;
    }

    // Should cover this in first pass
    public Type visit(Multiple m) throws XicException {
        assert false;
        return null;
    }

    public Type visit(Index i) throws XicException {
        Type it = i.index.accept(this);
        Type at = i.array.accept(this);
        
        if (!it.equals(Type.INT)) {
            throw new RuntimeException("Index is not integer");
        } else if (at.isClass()) {
            throw new RuntimeException("Not an array, silly");
        } else {
            return at.child;
        }
    }

    public Type visit(XiInt i) throws XicException {
        return Type.INT;
    }

    public Type visit(XiBool b) throws XicException {
        return Type.BOOL;
    }

    public Type visit(XiChar c) throws XicException {
        return Type.INT;
    }

    public Type visit(XiString s) throws XicException {
        return new Type(Type.INT);
    }

    public Type visit(XiArray a) throws XicException {
        if (a.values.size() == 0) {
            return Type.POLY; 
        } else {
            Type t = a.values.get(0).accept(this);

            for (int i = 1; i < a.values.size(); i++) {
                if (!t.equals(a.values.get(i).accept(this))) {
                    throw new RuntimeException("DERP0");
                }
            }
            return t;
        }
    }

    /*
     * Other nodes
     */

    public Type visit(XiType t) throws XicException {
        return new Type(t);
    }
}
