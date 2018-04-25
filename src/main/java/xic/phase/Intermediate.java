package xic.phase;

import xic.XicInternalException;
import xic.XicConfig;
import lex.XiLexer;
import ast.Program;
import ir.IRCompUnit;
import assemble.instructions.CompUnit;

public class Intermediate {
    
    public enum Kind {
        NONE,
        LEXER,
        AST,
        IR,
        ASM,
    }

    public final Kind kind;
    private XiLexer lexer;
    private Program ast;
    private IRCompUnit ir;
    private CompUnit assembly;

    /**
     * Empty intermediate
     */
    public Intermediate() {
        this.kind = Kind.NONE;
        this.lexer = null;
        this.ast = null;
        this.ir = null;
        this.assembly = null;
    }

    public Intermediate(XiLexer lexer) {
        this.kind = Kind.LEXER; 
        this.lexer = lexer;
        this.ast = null;
        this.ir = null;
        this.assembly = null;
    }

    public Intermediate(Program ast) {
        this.kind = Kind.AST;
        this.lexer = null;
        this.ast = ast;
        this.ir = null;
        this.assembly = null;
    }

    public Intermediate(IRCompUnit ir) {
        this.kind = Kind.IR;
        this.lexer = null;
        this.ast = null;
        this.ir = ir;
        this.assembly = null;
    }

    public Intermediate(CompUnit assembly) {
        this.kind = Kind.IR;
        this.lexer = null;
        this.ast = null;
        this.ir = null;
        this.assembly = assembly;
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

    public CompUnit getAssembly() {
        if (!(kind == Kind.ASM) || assembly == null) {
            throw XicInternalException.runtime("Could not retrieve assembly from intermediate.");
        }
        return assembly;
    }
}
