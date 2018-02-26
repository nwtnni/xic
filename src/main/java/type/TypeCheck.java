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
    	for (Node fn : p.fns) {
    		fn.accept(this);
    	} 
    	p.type = Type.UNIT;
    	return p.type;
    }

    public Type visit(Fn f) throws XicException {
    	Type args = f.args.accept(this);
    	
    	vars.push();
    	
    	switch (args.kind) {
    	case CLASS:
    		//TODO debugging purposes
    		assert args.hasVariable();
    		vars.add(args.getVariable(), args);
    		break;
    	case TUPLE:
    		for (Type child : args.children) {
    			assert child.hasVariable();
    			vars.add(child.getVariable(), child);
    		}
    		break;
    	default:
    		//TODO internal error
    		assert false;
    	}
    	
    	returns = fns.lookup(f.id).returns;
    	
        Type ft = f.block == null ? null : f.block.accept(this);
        vars.pop();
        switch (f.kind) {
        	case FN:
        		if (!ft.equals(Type.VOID)) {
        			throw new RuntimeException("Control reached end of non-void function");
        		}
        		break;
        	case PROC:
        		break;
        	default:
        		//TODO internal error
        		assert false;
        }
		f.type = Type.UNIT;
        return f.type;
    }

    /*
     * Statement nodes
     */
    public Type visit(Declare d) throws XicException {
    	if (d.isUnderscore()) {
    		d.type = new VarType("_", Type.UNIT);
    	}
    	else {
    		d.type = new VarType(d.id, d.xiType.accept(this));
    	}
    	return d.type;
    }

    public Type visit(Assign a) throws XicException {
    	Type lhs = a.lhs.accept(this);
    	Type rhs = a.rhs.accept(this);
    	
    	if (!lhs.equals(rhs)) { throw new RuntimeException("Mismatched types"); }

    	switch (lhs.kind) {
			case ARRAY:
			case CLASS:
				if (lhs.hasVariable()) {
					vars.add(lhs.getVariable(), lhs);	
				}
				break;
			case TUPLE:
				for (Type child : lhs.children) {
					if (child.hasVariable()) {
						vars.add(child.getVariable(), child);
					}
				}
				break;
		}
		
		a.type = Type.UNIT;
		return a.type;
    }

    public Type visit(Return r) throws XicException {
    	if ((r.hasValue() && returns.equals(r.value.accept(this))) 
    	|| (!r.hasValue() && returns.equals(Type.UNIT))) {
    		r.type = Type.VOID;
    		return r.type;
    	} else {
    		throw new RuntimeException("Mismatched return type");
    	}
    }

    public Type visit(Block b) throws XicException {

    	if (b.statements.size() == 0) { return Type.UNIT; }
    	
    	int last = b.statements.size() - 1;
    	for (int i = 0; i < last; i++) {
    		Type st = b.statements.get(i).accept(this);
    		if (st.equals(Type.VOID)) {
    			throw new RuntimeException("Unreachable statement");
    		} else if (!st.equals(Type.UNIT)) {
    			//TODO for debugging purposes
    			assert st.hasVariable();
    			vars.add(st.getVariable(), st);
    		}
    	}
    	
    	Type bt = b.statements.get(last).accept(this);
    	
    	if (bt.equals(Type.VOID)) {
    		b.type = Type.VOID;
    	} else if (bt.equals(Type.UNIT)) {
    		b.type = Type.UNIT;
    	} else {
    		//TODO internal error
    		assert false;
    	}
    	return b.type;
    }

    public Type visit(If i) throws XicException {
    	if (!i.guard.accept(this).equals(Type.BOOL)) {
    		throw new RuntimeException("Guard expression must be a boolean");
    	}
    	
    	vars.push();
    	Type it = i.block.accept(this);
    	vars.pop();
    	vars.push();
    	Type et = i.hasElse() ? i.elseBlock.accept(this) : null;
    	vars.pop();
    	
    	if (et != null && it.equals(Type.VOID) && et.equals(Type.VOID)) {
    		i.type = Type.VOID;
    	} else {
    		i.type = Type.UNIT;
    	}
    	return i.type;
    }
    
    public Type visit(While w) throws XicException {
    	if (!w.guard.accept(this).equals(Type.BOOL)) {
    		throw new RuntimeException("Guard expression must be a boolean");
    	}
    	
    	vars.push();
    	w.block.accept(this);
    	vars.pop();
    	w.type = Type.UNIT;
    	return w.type;
    }

    /*
     * Expression nodes
     */

    public Type visit(Call c) throws XicException {
    	Type args = c.args.accept(this);
    	FnType fn = fns.lookup(c.id);
    	
    	if (args.equals(fn.args)) {
    		c.type = fn.returns;
    		return c.type;
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
            	b.type = Type.BOOL;
            } else if (b.returnsInt()) {
            	b.type = Type.INT;
            } else {
                throw new TypeException(Kind.INVALID_INT_OP, b.location);
            }
        } else if (lt.equals(Type.BOOL) && b.acceptsBool()) {
            b.type = Type.BOOL;
        } else if (lt.kind.equals(Type.Kind.ARRAY) && b.acceptsList()) {
        	b.type = lt;
        } else {
        	throw new TypeException(Kind.INVALID_BIN_OP, b.location);
        }
        return b.type;
    }

    public Type visit(Unary u) throws XicException {
        Type ut = u.child.accept(this);
        if (u.isLogical()) {
            if (ut.equals(Type.BOOL)) {
            	u.type = Type.BOOL;
            } else {
                throw new TypeException(Kind.LNEG_ERROR, u.location);
            }
        } else {
            if (ut.equals(Type.INT)) {
                u.type = Type.INT;
            } else {
                throw new TypeException(Kind.NEG_ERROR, u.location);
            }
        }
        return u.type;
    }

    public Type visit(Var v) throws XicException {
    	try {
    		v.type = vars.lookup(v.id);
    		return v.type;
    	} catch (Exception todofixpls) {
    		throw new TypeException(TypeException.Kind.SYMBOL_NOT_FOUND, v.location);
    	}
    }

    public Type visit(Multiple m) throws XicException {
		ArrayList<Type> mt = new ArrayList<>();
		for (Node value : m.values) {
			mt.add(value.accept(this));
		}
		m.type = new Type(mt);
		return m.type;
    }

    public Type visit(Index i) throws XicException {
        Type it = i.index.accept(this);
        Type at = i.array.accept(this);

        if (!it.equals(Type.INT)) {
            throw new TypeException(Kind.INVALID_ARRAY_INDEX, i.index.location);
        } else if (at.kind != Type.Kind.ARRAY) {
            throw new TypeException(Kind.NOT_AN_ARRAY, i.array.location);
        } else {
        	//TODO make getter method?
        	i.type = at.children.get(0);
            return i.type;
        }
    }

    public Type visit(XiInt i) throws XicException {
        i.type = Type.INT;
    	return i.type;
    }

    public Type visit(XiBool b) throws XicException {
        b.type = Type.BOOL;
    	return b.type;
    }

    public Type visit(XiChar c) throws XicException {
        c.type = Type.INT;
    	return c.type;
    }

    public Type visit(XiString s) throws XicException {
        s.type = new Type(Type.INT);
    	return s.type;
    }

    public Type visit(XiArray a) throws XicException {
        if (a.values.size() == 0) {
            return Type.POLY;
        } else {
            Type at = a.values.get(0).accept(this);

            for (int i = 1; i < a.values.size(); i++) {
                if (!at.equals(a.values.get(i).accept(this))) {
                    throw new RuntimeException("DERP0");
                }
            }
            a.type = at;
            return a.type;
        }
    }

    /*
     * Other nodes
     */

    public Type visit(XiType t) throws XicException {
    	t.type = new Type(t);
        return t.type;
    }
}