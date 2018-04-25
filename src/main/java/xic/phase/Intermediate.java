package xic.phase;

import xic.XicInternalException;
import lex.XiLexer;
import ast.Program;
import ir.IRCompUnit;

public class Intermediate {
    
    public enum Kind {
        FILE,
        LEXER,
        AST,
        IR,
    }

    public final Kind kind;

    public final String source;
    public final String sink;
    public final String asm;
    public final String lib;
    public final String file;

    private XiLexer lexer;
    private Program ast;
    private IRCompUnit ir;

    /**
     * Initial intermediate: compilation
     */
    public Intermediate(String source, String sink, String lib, String asm, String file) {
        this.kind = Kind.FILE;
        this.source = source;
        this.sink = sink;
        this.lib = lib;
        this.asm = asm;
        this.file = file;
        this.lexer = null;
        this.ast = null;
        this.ir = null;
    }

    public Intermediate(Intermediate previous, XiLexer lexer) {
        this.kind = Kind.LEXER; 
        this.source = previous.source;
        this.sink = previous.sink; 
        this.asm = previous.asm;
        this.lib = previous.lib;
        this.file = previous.file;
        this.lexer = lexer;
        this.ast = null;
        this.ir = null;
    }

    public Intermediate(Intermediate previous, Program ast) {
        this.kind = Kind.AST;
        this.source = previous.source;
        this.sink = previous.sink; 
        this.asm = previous.asm;
        this.lib = previous.lib;
        this.file = previous.file;
        this.lexer = null;
        this.ast = ast;
        this.ir = null;
    }

    public Intermediate(Intermediate previous, IRCompUnit ir) {
        this.kind = Kind.IR;
        this.source = previous.source;
        this.sink = previous.sink; 
        this.asm = previous.asm;
        this.lib = previous.lib;
        this.file = previous.file;
        this.lexer = null;
        this.ast = null;
        this.ir = ir;
    }

    public XiLexer getLexer() {
        if (!(kind == Kind.LEXER) || lexer == null) {
            throw XicInternalException.runtime("Could not retrieve lexer from intermediate.");
        }
        return lexer;
    }

    public Program getAST() {
        if (!(kind == Kind.AST) || ast == null) {
            throw XicInternalException.runtime("Could not retrieve AST from intermediate.");
        }
        return ast;
    }

    public IRCompUnit getIR() {
        if (!(kind == Kind.IR) || ir == null) {
            throw XicInternalException.runtime("Could not retrieve IR from intermediate.");
        }
        return ir;
    }
}
