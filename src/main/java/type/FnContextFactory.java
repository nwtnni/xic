package type;

import ast.*;
import xic.XicException;

public class FnContextFactory extends Visitor<Void> {
    
    private static final FnContextFactory FACTORY = new FnContextFactory();
    private static FnContext context;
    
    public static FnContext from(Node ast) throws XicException {
        context = new FnContext(); 
        ast.accept(FACTORY);
        return context;
    }

    @Override
    public Void visit(Program p) throws XicException {
        for (Node f : p.fns) {
            f.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Fn f) throws XicException {
        context.add(f.id, new FnType(f));
        return null;
    }
}
