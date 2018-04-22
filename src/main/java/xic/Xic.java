package xic;

import assemble.Assembler;
import ast.Invariant;
import ast.Node;
import ast.Program;
import emit.ABIContext;
import emit.Canonizer;
import emit.ConstantFolder;
import emit.Emitter;
import interpret.IRSimulator;
import interpret.IRSimulator.Trap;
import ir.IRCompUnit;
import ir.IRNode;
import lex.LexException;
import lex.XiLexer;
import parse.IXiParser;
import parse.ParseException;
import parse.XiParser;
import type.FnContext;
import type.TypeChecker;
import type.TypeException;
import util.Filename;

/**
 * Main compiler class. Wraps around and provides convenience methods
 * for every phase of compilation.
 */
public class Xic {
    
	private XicConfig config;
	
    /**
     * Creates a new compiler instance which will read from the given
     * file paths.
     */
    public Xic(XicConfig config) {
    	this.config = config;
    }

    /**
     * Creates a {@link lex.XiLexer} which reads the input file.
     * 
     * @param unit The path to the input file, relative to source
     * @return A XiLexer instance which will read the input file
     * @throws XicException if lexing failed
     */
    public XiLexer lex(String unit) throws XicException {
        switch (Filename.getExtension(unit)) {
            case "xi":
            case "ixi":
                return XiLexer.from(config.source, unit);
            default:
                throw XicException.unsupported(unit);
        }
    }

    /**
     * Returns the AST associated with the input file.
     * 
     * @param unit The path to the input file, relative to source
     * @return The AST of the input file
     * @throws XicException if lexing or parsing failed
     */
    public Node parse(String unit) throws XicException {
        Node ast = null;
        
        switch (Filename.getExtension(unit)) {
            case "xi":
                ast = XiParser.from(config.source, unit);
                break;
            case "ixi":
                ast = IXiParser.from(config.source, unit);
                break;
            default:
                throw XicException.unsupported(unit);
        }
        
        Invariant.check(ast);
        return ast;
    }
    
    /**
     * Decorates the provided AST with type annotations. Mutates the given AST.
     * 
     * @param ast The undecorated AST
     * @return The function context associated with the provided AST
     * @throws XicException if typechecking failed
     */
    public FnContext type(Node ast) throws XicException {
        return TypeChecker.check(config.lib, ast);
    }
    
    public IRNode emit(FnContext context, Node ast) throws XicException {
    	IRNode comp = Emitter.emitIR(ast, context);
    	if (config.optimize) ConstantFolder.constantFold(comp);
    	return Canonizer.canonize(comp);
    }
    
    public void interpret(IRNode ast) {
        try {
            IRSimulator sim = new IRSimulator((IRCompUnit) ast);
            sim.call("_Imain_paai", 0);
        } catch (Trap e) {
            System.out.println(e.getMessage());
        }
    }
    
    public String assemble(FnContext context, IRNode program) throws XicException {
    	return Assembler.assemble(program, new ABIContext(context));
    }
    
    public void execute() throws XicException {
    	
    	for (String unit : config.files) {
    		
    		try {
    			XiLexer lexer = lex(unit);
    			
    			
    			Node ast = parse(unit);
    			FnContext context = type(ast);
    			IRNode program = emit(context, ast);
    			if (config.interpret) interpret(program);
    			String assembly = assemble(context, program);
    		} catch (LexException le) {
    			
    			
    		} catch (ParseException pe) {
    			
    			
    		} catch (TypeException te) {
    			
    		}
    	}
    }
}