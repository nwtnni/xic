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
    private Type returns;

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
    	if (d.isUnderscore()) {
    		return new VarType("_", Type.UNIT);
    	}
    	else {
    		return new VarType(d.id, d.type.accept(this));
    	}
    }

    public Type visit(Assign a) throws XicException {
    	Type lhs = a.lhs.accept(this);
    	Type rhs = a.rhs.accept(this);
    	
    	if (!lhs.equals(rhs)) { throw new RuntimeException("Mismatched types"); }

    	switch (lhs.kind) {
			case ARRAY:
			case CLASS:
				vars.add(lhs.getVariable(), lhs);
				break;
			case TUPLE:
				for (Type child : lhs.children) {
					vars.add(child.getVariable(), child);
				}
				break;
		}
		
		a.type = Type.UNIT;
		return Type.UNIT;
    }

    public Type visit(Return r) throws XicException {
    	if ((r.hasValue() && returns.equals(r.value.accept(this))) 
    	|| (!r.hasValue() && returns.equals(Type.EMPTY))) {
    		r.type = Type.VOID;
    		return Type.VOID;
    	} else {
    		throw new RuntimeException("Mismatched return type");
    	}
    }

    public Type visit(Block b) throws XicException {

    	vars.push();
    	int last = b.statements.size() - 1;
    	for (int i = 0; i < last; i++) {
    		if (b.statements.get(i).accept(this).equals(Type.VOID)) {
    			throw new RuntimeException("Unreachable statement");
    		}
    	}
    	
    	Type bt = b.statements.get(last).accept(this);
    	vars.pop();
    	
    	if (bt.equals(Type.VOID)) {
    		b.type = Type.VOID;
    		return Type.VOID;
    	} else if (bt.equals(Type.UNIT)) {
    		b.type = Type.UNIT;
    		return Type.UNIT;
    	} else {
    		//TODO internal error
    		assert false;
    		return null;
    	}
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
    	Type args = c.args.accept(this);
    	FnType fn = fns.lookup(c.id);
    	
    	if (args.equals(fn.args)) {
    		return fn.returns;
    	} else {
    		throw new TypeException(Kind.INVALID_ARG_TYPES, c.location);
    	}
    }

    public Type visit(Binary b) throws XicException {
        Type lt = b.lhs.accept(this);
        Type rt = b.rhs.accept(this);

        if (!lt.equals(rt)) { 
            throw new TypeException(Kind.MISMATCHED_TYPES, b.location);
        }

        if (lt.equals(Type.INT) && b.acceptsInt()) {
            if (b.returnsBool()) {
                return Type.BOOL;
            } else if (b.returnsInt()) {
                return Type.INT;
            } else {
                throw new TypeException(Kind.INVALID_INT_OP, b.location);
            }
        }

        if (lt.equals(Type.BOOL) && b.acceptsBool()) {
            return Type.BOOL;
        }

        if (lt.kind == Type.Kind.ARRAY && b.acceptsList()) {
            return lt;
        }

        throw new TypeException(Kind.INVALID_BIN_OP, b.location);
    }

    public Type visit(Unary u) throws XicException {
        Type ut = u.child.accept(this);
        if (u.isLogical()) {
            if (ut.equals(Type.BOOL)) {
                return Type.BOOL;
            } else {
                throw new TypeException(Kind.LNEG_ERROR, u.location);
            }
        } else {
            if (ut.equals(Type.INT)) {
                return Type.INT;
            } else {
                throw new TypeException(Kind.NEG_ERROR, u.location);
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
            throw new TypeException(Kind.INVALID_ARRAY_INDEX, i.index.location);
        } else if (at.kind != Type.Kind.ARRAY) {
            throw new TypeException(Kind.NOT_AN_ARRAY, i.array.location);
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