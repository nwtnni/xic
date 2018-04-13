package xic;

import ast.Invariant;
import ast.Node;
import lex.XiLexer;
import parse.IXiParser;
import parse.XiParser;
import type.TypeChecker;
import util.Filename;

/**
 * Main compiler class. Wraps around and provides convenience methods
 * for every phase of compilation.
 */
public class Xic {
    
    /**
     * The source path associated with this compiler, i.e. where it will
     * look for source files.
     */
    private String source;
    
    /**
     * The sink path associated with this compiler, i.e. where it will
     * output diagnostic files.
     */
    private String sink;
    
    /**
     * The lib path associated with this compiler, i.e. where it will
     * look for interface files.
     */
    private String lib;
    
    /**
     * Creates a new compiler instance which will read from the given
     * file paths.
     * 
     * @param source Where to look for source files
     * @param sink Where to output diagnostic files
     * @param lib Where to look for interface files
     */
    public Xic(String source, String sink, String lib) {
        this.source = source;
        this.sink = sink;
        this.lib = lib;
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
                return XiLexer.from(source, unit);
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
                ast = XiParser.from(source, unit);
                break;
            case "ixi":
                ast = IXiParser.from(source, unit);
                break;
            default:
                throw XicException.unsupported(unit);
        }
        
        Invariant.check(ast);
        return ast;
    }
    
    /**
     * Returns the decorated AST (i.e. with type annotations) 
     * associated with the input file.
     * 
     * @param unit The path to the input file, relative to source
     * @return The decorated AST of the input file
     * @throws XicException if lexing, parsing, or typechecking failed
     */
    public Node typeCheck(String unit) throws XicException {
        Node ast = parse(unit);
        TypeChecker.check(lib, ast);
        return ast;
    }
    
    /**
     * Prints and writes diagnostics for the lexed input file.
     * 
     * @param unit The path to the input file, relative to source
     * @throws XicException if lexing failed
     */
    public void printLexed(String unit) throws XicException {
        lex.Printer.print(source, sink, unit);
    }
    
    /**
     * Prints and writes diagnostics for the lexed and parsed input file.
     * 
     * @param unit The path to the input file, relative to source
     * @throws XicException if lexing or parsing failed
     */
    public void printParsed(String unit) throws XicException {
        parse.Printer.print(source, sink, unit);
    }

    /**
     * Prints and writes diagnostics for the lexed, parsed, and typechecked input
     * file.
     * 
     * @param unit The path to the input file, relative to source
     * @throws XicException if lexing, parsing, or typechecking failed
     */
    public void printTyped(String unit) throws XicException {
        type.Printer.print(source, sink, lib, unit);
    }

    /**
     * Prints and writes diagnostics for the lexed, parsed, and typechecked input
     * file.
     * 
     * @param unit The path to the input file, relative to source
     * @param run Run IR interpreter on generated IR code
     * @param opt enable optimizations if true 
     * @throws XicException if lexing, parsing, or typechecking failed
     */
    public void printIR(String unit, boolean run, boolean opt) throws XicException {
        ir.Printer.print(source, sink, lib, unit, run, opt);
    }

    /**
     * Prints and writes diagnostics for the lexed, parsed, and typechecked input
     * file.
     * 
     * @param unit The path to the input file, relative to source
     * @throws XicException if lexing, parsing, or typechecking failed
     */
    public void printAssembly(String unit, boolean opt, boolean assemblyFlag, String assemblySink) throws XicException {
        if (assemblyFlag) {
            assemble.Printer.print(source, assemblySink, lib, unit, opt);
        } else {
            assemble.Printer.print(source, sink, lib, unit, opt);
        }
    }
}