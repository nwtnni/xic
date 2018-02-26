package type;

import java.util.ArrayList;

import ast.*;
import type.TypeException.Kind;
import xic.Xic;
import xic.XicException;

public class TypeCheck extends Visitor<Type> {

    public static void check(String source, Node ast) throws XicException {
    	ast.accept(new TypeCheck(source, ast));
    }
    
    private TypeCheck(String source, Node ast) throws XicException {
    	this.fns = UseImporter.resolve(source, ast);
    	this.types = new TypeContext();
    	this.vars = new VarContext();
    }

    private TypeContext types;
    private FnContext fns;
    private VarContext vars;
    private ArrayList<Type> expected;

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

    public Type visit(Call c) throws XicException {
    	
    	ArrayList<Type> types = new ArrayList<>();
    	for (Node arg : c.args) {
    		types.add(arg.accept(this));
    	}
    	Type args = new Type(types);
    	FnType fn = fns.lookup(c.id);
    	
    	if (args.equals(fn.args)) {
    		return fn.returns;
    	} else {
    		throw new RuntimeException("Function call with incorrect arguments");
    	}
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

        if (lt.kind == Type.Kind.ARRAY && b.isList()) {
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

    public Type visit(Var v) throws XicException {
    	try {
    		return vars.lookup(v.id);
    	} catch (Exception todofixpls) {
    		throw new TypeException(TypeException.Kind.SYMBOL_NOT_FOUND, v.location);
    	}
    }

    public Type visit(Multiple m) throws XicException {
		ArrayList<Type> types = new ArrayList<>();
		for (Node value : m.values) {
			types.add(value.accept(this));
		}
		return new Type(types);
    }

    public Type visit(Index i) throws XicException {
        Type it = i.index.accept(this);
        Type at = i.array.accept(this);

        if (!it.equals(Type.INT)) {
            throw new RuntimeException("Index is not integer");
        } else if (at.kind != Type.Kind.ARRAY) {
            throw new RuntimeException("Not an array, silly");
        } else {
            return at.children.get(0);
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