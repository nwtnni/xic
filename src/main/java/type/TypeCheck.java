package type;

import org.pcollections.*;;
import ast.*;

public class TypeCheck implements Visitor<Type> {

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
    public Type visit(Call c) {
        //TODO
        return null;
    }

    public Type visit(Binary b) {
        //TODO
        return null;
    }

    public Type visit(Unary u) {
        //TODO
        return null;
    }

    public Type visit(Variable v) {
        //TODO
        return null;
    }

    public Type visit(Multiple m) {
        //TODO
        return null;
    }

    public Type visit(Index i) {
        //TODO
        return null;
    }

    public Type visit(XiInt i) {
        //TODO
        return null;
    }

    public Type visit(XiBool b) {
        //TODO
        return null;
    }

    public Type visit(XiChar c) {
        //TODO
        return null;
    }

    public Type visit(XiString s) {
        //TODO
        return null;
    }

    public Type visit(XiArray a) {
        //TODO
        return null;
    }

    /*
     * Other nodes
     */

    public Type visit(XiType t) {
        //TODO
        return null;
    }
}
