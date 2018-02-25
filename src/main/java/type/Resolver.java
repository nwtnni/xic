package type;

import ast.*;
import xic.Xic;
import xic.XicException;

public class Resolver extends Visitor<Void> {
    
    private FnContext context;
    private Xic xic;
    
    public static FnContext resolve(Xic xic, Node ast) throws XicException {
    	Resolver resolver = new Resolver(xic);
    	return resolver.context;
    }
    
    private Resolver(Xic xic) {
    	this.xic = xic;
    	this.context = new FnContext();
    }

    @Override
    public Void visit(Program p) throws XicException {
        for (Node u : p.uses) {
        	u.accept(this);
        }
    	
    	for (Node f : p.fns) {
            f.accept(this);
        }
        return null;
    }
    
    public Void visit(Use u) throws XicException {
    	Node ast = xic.parse(u.file + ".ixi");
    	FnContext header = Resolver.resolve(xic, ast);
    	context.merge(header);
    	return null;
    }

    @Override
    public Void visit(Fn f) throws XicException {
        context.add(f.id, FnType.from(f));
        return null;
    }
}
