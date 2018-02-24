package type;

import ast.*;

public class FunctionContextFactory extends Visitor<Void> {
    
    private static final FunctionContextFactory FACTORY = new FunctionContextFactory();
    private static FunctionContext fc;

    @Override
    public Void visit(Program p) {
        for (Node f : p.functions) {
            f.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Function f) {
        fc.add(f.id, new FunctionType(f));
        return null;
    }
    
    public static FunctionContext from(Node ast) {
        fc = new FunctionContext(); 
        ast.accept(FACTORY);
        return fc;
    }
}
