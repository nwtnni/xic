package type;

import java.util.ArrayList;

import ast.*;
import type.TypeException.Kind;
import xic.XicException;

/**
 * Main type checking implementation. Recursively traverses the AST
 * and verifies typing rules at each node, as defined by the Xi Type
 * Specification. This implementation mutates the provided AST, decorating
 * each node with a Type field.
 */
public class TypeChecker extends Visitor<Type> {

	/**
	 * Factory method to type check the given AST and return the 
	 * associated function context.
	 * @param lib Directory to search for interface files
	 * @param ast AST to typecheck
	 * @throws XicException if a semantic error was found
	 */
	public static FnContext check(String lib, Node ast) throws XicException {
		TypeChecker checker = new TypeChecker(lib, ast);
		ast.accept(checker);
		return checker.fns;
	}

	/**
	 * Default constructor initializes empty contexts.
	 */
	protected TypeChecker() {
		this.fns = new FnContext();
		this.types = new TypeContext();
		this.vars = new VarContext();
	}

	/**
	 * This constructor initializes the FnContext with {@link Importer},
	 * but leaves the other empty.
	 * 
	 * @param lib Directory to search for interface files
	 * @param ast AST to resolve dependencies for
	 * @throws XicException if a semantic error occurred while resolving dependencies
	 */
	private TypeChecker(String lib, Node ast) throws XicException {
		this.fns = Importer.resolve(lib, ast);
		this.types = new TypeContext();
		this.vars = new VarContext();
	}

	/**
	 * Associated function context.
	 */
	protected FnContext fns;

	/**
	 * Associated variable context.
	 */
	protected VarContext vars;
	
	/**
	 * Associated type context.
	 */
	private TypeContext types;
	
	/**
	 * The current value of rho, the expected return
	 * type for the current function in scope, as defined
	 * in the type specification.
	 */
	private Type returns;

	/*
	 * Top-level AST nodes
	 */
	
	/**
	 * A program is valid if all of its top-level declarations
	 * are valid. Use statements and top-level declarations 
	 * are checked by {@link Importer},
	 * while function bodies are checked by this class.
	 * 
	 * @returns {@link Type.UNIT} if program is valid
	 * @throws XicException if program has semantic errors
	 */
	public Type visit(Program p) throws XicException {
		for (Node fn : p.fns) {
			fn.accept(this);
		}
		p.type = Type.UNIT;
		return p.type;
	}

	/**
	 * A function is valid if none of its arguments
	 * shadow anything in the context, and its block is
	 * void if it has return types.
	 * 
	 * @returns {@link Type.UNIT} is function is valid
	 * @throws XicException if function has semantic errors
	 */
	public Type visit(Fn f) throws XicException {

		vars.push();
		f.args.accept(this);

		FnType fn = fns.lookup(f.id);

		if (fn == null) {
			// Internal error occurred; should never happen
			throw new TypeException(Kind.SYMBOL_NOT_FOUND, f.location);
		}

		returns = fn.returns;

		Type ft = f.block.accept(this);
		vars.pop();

		if (f.isFn() && !ft.equals(Type.VOID)) {
			throw new TypeException(Kind.CONTROL_FLOW, f.location);
		}

		f.type = Type.UNIT;
		return f.type;
	}

	/*
	 * Statement nodes
	 */
	
	/**
	 * A declaration is valid if it doesn't shadow anything in the context.
	 * 
	 * @returns typeof(declaration) if valid
	 * @throws XicException if a conflict was found
	 */
	public Type visit(Declare d) throws XicException {
		if (d.isUnderscore()) {
			d.type = Type.UNIT;
		} else if (vars.contains(d.id) || fns.contains(d.id)) {
			throw new TypeException(Kind.DECLARATION_CONFLICT, d.location);
		} else {
			d.type = d.xiType.accept(this);
			vars.add(d.id, d.type);
		}
		return d.type;
	}

	/**
	 * An assignment is valid if each type on the RHS is a subtype of the
	 * corresponding type on the LHS, and the number of types is matched.
	 * 
	 * Additionally, a procedure cannot be assigned to anything, and only
	 * function calls can have wildcards on the LHS.
	 * 
	 * @returns {@link Type.UNIT} if valid
	 * @throws XicException if invalid assignment
	 */
	public Type visit(Assign a) throws XicException {
		Type rt = a.rhs.accept(this);
		Type lt = a.lhs.accept(this);

		if (!types.isSubType(rt, lt)) {
			throw new TypeException(Kind.MISMATCHED_ASSIGN, a.location);
		}

		if (lt.equals(Type.UNIT) && !(a.rhs instanceof Call)) {
			throw new TypeException(Kind.INVALID_WILDCARD, a.location);
		}

		a.type = Type.UNIT;
		return a.type;
	}

	/**
	 * A return is valid if its type matches {@link TypeChecker#returns}
	 * 
	 * @returns {@link Type.VOID} if return type matches {@link TypeChecker#returns}
	 * @throws XicException if return type doesn't match
	 */
	public Type visit(Return r) throws XicException {
		if (r.hasValue()) {
			Type value = r.value.accept(this);
			if (!value.equals(Type.UNIT) && returns.equals(value)) {
				r.type = Type.VOID;
				return r.type;
			}
		} else if (returns.equals(Type.UNIT)) {
			r.type = Type.VOID;
			return r.type;
		}
		throw new TypeException(Kind.MISMATCHED_RETURN, r.location);
	}

	/**
	 * A block is valid if each statement is valid, and no statement before the
	 * last one is type {@link Type.VOID}.
	 * 
	 * @returns The type of the last statement
	 * @throws XicException if invalid
	 */
	public Type visit(Block b) throws XicException {
		b.type = Type.UNIT;
		vars.push();
		int size = b.statements.size();

		for (int i = 0; i < size; i++) {

			Node s = b.statements.get(i);
			Type st = s.accept(this);

			// Unused function result
			if (!st.equals(Type.VOID) && !st.equals(Type.UNIT) && s instanceof Call) {
				throw new TypeException(Kind.UNUSED_FUNCTION, b.statements.get(i).location);
			}

			// Unreachable code
			if (i < size - 1 && st.equals(Type.VOID)) {
				throw new TypeException(Kind.UNREACHABLE, b.statements.get(i + 1).location);
			} else {
				b.type = st.equals(Type.VOID) ? Type.VOID : Type.UNIT;
			}
		}
		vars.pop();
		return b.type;
	}

	/**
	 * An if statement is valid if its guard is {@link Type.BOOL} and its block is valid.
	 * 
	 * @returns If both blocks are {@link Type.VOID}, then Type.VOID, otherwise Type.UNIT
	 * @throws XicException if invalid
	 */
	public Type visit(If i) throws XicException {
		if (!i.guard.accept(this).equals(Type.BOOL)) {
			throw new TypeException(Kind.INVALID_GUARD, i.guard.location);
		}

		Type it = i.block.accept(this);
		Type et = i.hasElse() ? i.elseBlock.accept(this) : null;

		if (et != null && it.equals(Type.VOID) && et.equals(Type.VOID)) {
			i.type = Type.VOID;
		} else {
			i.type = Type.UNIT;
		}
		return i.type;
	}

	/**
	 * A while statement is valid if its guard is {@link Type.BOOL} and its block is valid.
	 * 
	 * @returns {@link Type.UNIT} if valid
	 * @throws XicException if invalid
	 */
	public Type visit(While w) throws XicException {
		if (!w.guard.accept(this).equals(Type.BOOL)) {
			throw new TypeException(Kind.INVALID_GUARD, w.guard.location);
		}

		w.block.accept(this);
		w.type = Type.UNIT;
		return w.type;
	}

	/*
	 * Expression nodes
	 */

	/**
	 * A function call is valid if the arguments match the function's arguments.
	 */
	public Type visit(Call c) throws XicException {
		if (c.id.equals("length")) {
			Type args = c.args.accept(this);
			if (!args.kind.equals(Type.Kind.ARRAY)) {
				throw new TypeException(Kind.NOT_AN_ARRAY, c.location);
			}
			c.type = Type.INT;
			return c.type;
		} else {
			FnType fn = fns.lookup(c.id);
			if (fn == null) {
				throw new TypeException(Kind.SYMBOL_NOT_FOUND, c.location);
			}

			Type args = c.args.accept(this);

			if (args.equals(fn.args)) {
				c.type = fn.returns;
				return c.type;
			} else {
				throw new TypeException(Kind.INVALID_ARG_TYPES, c.location);
			}
		}
	}

	/**
	 * A binary operation is valid if the types of the operands and the operator match.
	 */
	public Type visit(Binary b) throws XicException {
		Type lt = b.lhs.accept(this);
		Type rt = b.rhs.accept(this);

		if (!lt.equals(rt)) {
			throw new TypeException(Kind.MISMATCHED_BINARY, b.location);
		}

		if (lt.equals(Type.INT) && b.acceptsInt()) {
			if (b.returnsBool()) {
				b.type = Type.BOOL;
			} else {
				b.type = Type.INT;
			} 
		} else if (lt.equals(Type.BOOL) && b.acceptsBool()) {
			b.type = Type.BOOL;
		} else if (lt.kind.equals(Type.Kind.ARRAY) && b.acceptsList()) {
			if (b.returnsBool()) {
				b.type = Type.BOOL;
			} else {
				b.type = lt;
			}
		} else {
			throw new TypeException(Kind.INVALID_BIN_OP, b.location);
		}
		return b.type;
	}

	/**
	 * A unary operator is valid if the type of the operator and operand match.
	 * 
	 * @returns The type of the operator if valid
	 * @throws XicException if operator mismatch
	 */
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

	/**
	 * A variable lookup is valid if the variable exists in the context.
	 * 
	 * @returns typeof(variable) if valid
	 * @throws XicException if invalid
	 */
	public Type visit(Var v) throws XicException {
		v.type = vars.lookup(v.id);
		if (v.type == null) {
			throw new TypeException(TypeException.Kind.SYMBOL_NOT_FOUND, v.location);
		}
		return v.type;
	}

	/**
	 * A multiple nonterminal is valid if its children are valid.
	 * 
	 * @returns Tuple of child types if valid
	 * @throws XicException if invalid
	 */
	public Type visit(Multiple m) throws XicException {
		// TODO: remove when we convert UNIT to a tuple type
		if (m.values.size() == 0) {
			m.type = Type.UNIT;
			return Type.UNIT;
		}

		ArrayList<Type> types = new ArrayList<>();
		for (Node value : m.values) {
			types.add(value.accept(this));
		}
		switch (m.kind) {
			case ASSIGN:
			case RETURN:
			case FN_RETURNS:
				m.type = new Type(types, false);
				return m.type;
			case FN_CALL:
			case FN_ARGS:
				m.type = new Type(types, true);
				return m.type;
		}
		// Unreachable
		assert false;
		return null;
	}

	/**
	 * An array index is valid if the array child is {@link Type.Kind.ARRAY}, and the
	 * index child is {@link Type.INT}
	 */
	public Type visit(Index i) throws XicException {
		Type it = i.index.accept(this);
		Type at = i.array.accept(this);

		if (!it.equals(Type.INT)) {
			throw new TypeException(Kind.INVALID_ARRAY_INDEX, i.index.location);
		} else if (at.kind != Type.Kind.ARRAY) {
			throw new TypeException(Kind.NOT_AN_ARRAY, i.array.location);
		} else {
			i.type = at.children.get(0);
			return i.type;
		}
	}

	/**
	 * A XiInt is always {@link Type.INT}
	 * 
	 * @returns {@link Type.INT}
	 */
	public Type visit(XiInt i) {
		i.type = Type.INT;
		return i.type;
	}

	/**
	 * A XiBool is always {@link Type.BOOL}
	 * 
	 * @returns {@link Type.BOOL}
	 */
	public Type visit(XiBool b) {
		b.type = Type.BOOL;
		return b.type;
	}

	/**
	 * A XiChar is always {@link Type.INT}
	 * 
	 * @returns {@link Type.INT}
	 */
	public Type visit(XiChar c) {
		c.type = Type.INT;
		return c.type;
	}

	/**
	 * A XiString is always a {@link Type.Kind.ARRAY} of {@link Type.INT}
	 * 
	 * @returns Array of {@link Type.INT}
	 */
	public Type visit(XiString s) {
		s.type = new Type(Type.INT);
		return s.type;
	}

	/**
	 * A XiArray is valid if its children are the same type.
	 * 
	 * The 0-length array is polymorphic and has special type {@link Type.POLY},
	 * which is equal to all array types.
	 * 
	 * @returns Array of child types
	 * @throws XicException if invalid
	 */
	public Type visit(XiArray a) throws XicException {
		if (a.values.size() == 0) {
			a.type = Type.POLY;
			return Type.POLY;
		} else {
			Type arrayType = a.values.get(0).accept(this);

			for (int i = 1; i < a.values.size(); i++) {
				Type elemType = a.values.get(i).accept(this);
				if (arrayType.isPoly()) {
					arrayType = elemType;
				} else if (!arrayType.equals(elemType)) {
					throw new TypeException(Kind.NOT_UNIFORM_ARRAY, a.location);
				}
			}

			// Iterate through elements again to coerce all types 
			for (int i = 0; i < a.values.size(); i++) {
				Type elemType = a.values.get(i).accept(this);
				elemType.equals(arrayType);
			}
			
			a.type = new Type(arrayType);
			return a.type;
		}
	}

	/**
	 * A XiType is equal to its corresponding Type.
	 * 
	 * @returns Corresponding type
	 * @throws XicException if array type with invalid size
	 */
	public Type visit(XiType t) throws XicException {
		t.type = new Type(t);
		
		if (t.hasSize() && !t.size.accept(this).equals(Type.INT)) {
			throw new TypeException(Kind.INVALID_ARRAY_SIZE, t.size.location);
		}
		
		return t.type;
	}
}
