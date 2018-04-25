package xic.phase;

import ast.Program;
import assemble.instructions.CompUnit;
import emit.ABIContext;
import ir.IRCompUnit;
import lex.XiLexer;
import type.FnContext;
import util.Pair;
import xic.XicInternalException;

public class Product {

    private final XiLexer lexed;
    private final Program parsed;
    private final Pair<Program, FnContext> typed;
    private final Pair<IRCompUnit, ABIContext> emitted;
    private final CompUnit assembled;

    private Product (XiLexer l, Program p, Pair<Program, FnContext> t, Pair<IRCompUnit, ABIContext> e, CompUnit a) {
        lexed = l; 
        parsed = p;
        typed = t;
        emitted = e;
        assembled = a;
    }

    public static Product empty() { return new Product(null, null, null, null, null); }

    public static Product lexed(XiLexer l) { return new Product(l, null, null, null, null); }

    public static Product parsed(Program p) { return new Product(null, p, null, null, null); }

    public static Product typed(Pair<Program, FnContext> t) { return new Product(null, null, t, null, null); }

    public static Product emitted(Pair<IRCompUnit, ABIContext> e) { return new Product(null, null, null, e, null); }

    public static Product assembled(CompUnit a) { return new Product(null, null, null, null, a); }

    public XiLexer getLexed() {
        if (lexed == null) {
            throw XicInternalException.runtime("Could not retrieve lexer from intermediate.");
        }
        return lexed;
    }

    public Program getParsed() {
        if (parsed == null) {
            throw XicInternalException.runtime("Could not retrieve AST from intermediate.");
        }
        return parsed;
    }

    public Pair<Program, FnContext> getTyped() {
        if (typed == null) {
            throw XicInternalException.runtime("Could not retrieve typed AST from intermediate.");
        }
        return typed;
    }

    public Pair<IRCompUnit, ABIContext> getEmitted() {
        if (emitted == null) {
            throw XicInternalException.runtime("Could not retrieve IR from intermediate.");
        }
        return emitted;
    }

    public CompUnit getAssembled() {
        if (assembled == null) {
            throw XicInternalException.runtime("Could not retrieve assembly from intermediate.");
        }
        return assembled;
    }
}
