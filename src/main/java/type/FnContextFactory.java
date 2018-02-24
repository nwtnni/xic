package type;

import ast.*;

public class FnContextFactory extends Visitor<Void> {
    
    private static final FnContextFactory FACTORY = new FnContextFactory();
    private static FnContext context;
    
    public static FnContext from(Node ast) {
        context = new FnContext(); 
        ast.accept(FACTORY);
        return context;
    }

    @Override
    public Void visit(Program p) {
        for (Node f : p.fns) {
            f.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Fn f) {
        context.add(f.id, new FnType(f));
        return null;
    }
}
