package type;

import java.util.ArrayList;
import org.pcollections.*;;
import ast.*;

public class TypeCheck extends Visitor<Type> {

    public static final TypeCheck CHECKER = new TypeCheck();

    private PMap<String, Type> context;

    private TypeCheck() {
        this.context = HashTreePMap.empty();
    }

    public static boolean check() {
        return true;
    }

    /*
     * Top-level AST nodes
     */
    public Type visit(Program p) {
        //TODO
        return null;
    }

    public Type visit(Use u) {
        //TODO
        return null;
    }

    public Type visit(Function f) {
        //TODO
        return null;
    }

    /*
     * Statement nodes
     */
    public Type visit(Declare d) {
        //TODO
        return null;
    }

    public Type visit(Assign a) {
        //TODO
        return null;
    }

    public Type visit(Return r) {
        //TODO
        return null;
    }

    public Type visit(Block b) {
        //TODO
        return null;
    }

    public Type visit(If i) {
        //TODO
        return null;
    }

    public Type visit(Else e) {
        //TODO
        return null;
    }

    public Type visit(While w) {
        //TODO
        return null;
    }

    /*
     * Expression nodes
     */

    // TODO
    // Context dependent
    public Type visit(Call c) {
        assert false;
        return null;
    }

    public Type visit(Binary b) {
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

    public Type visit(Unary u) {
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
    public Type visit(Variable v) {
        assert false;
        return null;
    }

    // Should cover this in first pass
    public Type visit(Multiple m) {
        assert false;
        return null;
    }

    public Type visit(Index i) {
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

    public Type visit(XiInt i) {
        return Type.INT;
    }

    public Type visit(XiBool b) {
        return Type.BOOL;
    }

    public Type visit(XiChar c) {
        return Type.INT;
    }

    public Type visit(XiString s) {
        return new Type(Type.INT);
    }

    public Type visit(XiArray a) {
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

    public Type visit(XiType t) {
        return new Type(t);
    }
}
