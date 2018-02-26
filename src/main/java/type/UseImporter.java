package type;

import ast.*;
import parser.IXiParser;
import xic.XicException;

public class UseImporter extends Visitor<Void> {
    
    private FnContext context;
    private String source;
    
    public static FnContext resolve(String source, Node ast) throws XicException {
    	UseImporter resolver = new UseImporter(source);
    	ast.accept(resolver);
    	return resolver.context;
    }
    
    private UseImporter(String source) {
    	this.source = source;
    	this.context = new FnContext();
    }

    @Override
    public Void visit(Program p) throws XicException {
    	if (p.isProgram()) {
            for (Node u : p.uses) { u.accept(this); }
    	}
    	for (Node f : p.fns) { f.accept(this); }
        return null;
    }
    
    public Void visit(Use u) throws XicException {
    	Node ast = IXiParser.from(source, u.file + ".ixi");
    	FnContext header = UseImporter.resolve(source, ast);
    	context.merge(header);
    	return null;
    }

    @Override
    public Void visit(Fn f) throws XicException {
        context.add(f.id, FnType.from(f));
        return null;
    }
}