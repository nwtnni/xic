package xic.phase;

import assemble.CompUnit;
import assemble.Reg;
import assemble.Temp;
import ast.XiProgram;
import emit.ABIContext;
import ir.IRCompUnit;
import lex.XiLexer;
import type.FnContext;
import util.Pair;
import xic.XicInternalException;

/**
 * Internal variant class representing an intermediate compiler product.
 *
 * Used to transport intermediates between various compiler
 * phases.
 */
public class Product {

    private final XiLexer lexed;
    private final XiProgram parsed;
    private final Pair<XiProgram, FnContext> typed;
    private final Pair<IRCompUnit, ABIContext> emitted;
    private final CompUnit<Temp> assembled;
    private final CompUnit<Reg> allocated;

    private Product (XiLexer l, XiProgram p, Pair<XiProgram, FnContext> t, Pair<IRCompUnit, ABIContext> e, CompUnit<Temp> a, CompUnit<Reg> r) {
        lexed = l; 
        parsed = p;
        typed = t;
        emitted = e;
        assembled = a;
        allocated = r;
    }

    public static Product empty() { return new Product(null, null, null, null, null, null); }

    public static Product lexed(XiLexer l) { return new Product(l, null, null, null, null, null); }

    public static Product parsed(XiProgram p) { return new Product(null, p, null, null, null, null); }

    public static Product typed(Pair<XiProgram, FnContext> t) { return new Product(null, null, t, null, null, null); }

    public static Product emitted(Pair<IRCompUnit, ABIContext> e) { return new Product(null, null, null, e, null, null); }

    public static Product assembled(CompUnit<Temp> a) { return new Product(null, null, null, null, a, null); }

    public static Product allocated(CompUnit<Reg> a) { return new Product(null, null, null, null, null, a); }

    public XiLexer getLexed() {
        if (lexed == null) {
            throw XicInternalException.runtime("Could not retrieve lexer from intermediate.");
        }
        return lexed;
    }

    public XiProgram getParsed() {
        if (parsed == null) {
            throw XicInternalException.runtime("Could not retrieve AST from intermediate.");
        }
        return parsed;
    }

    public Pair<XiProgram, FnContext> getTyped() {
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

    public CompUnit<Temp> getAssembled() {
        if (assembled == null) {
            throw XicInternalException.runtime("Could not retrieve assembly from intermediate.");
        }
        return assembled;
    }

    public CompUnit<Reg> getAllocated() {
        if (allocated == null) {
            throw XicInternalException.runtime("Could not retrieve assembly from intermediate.");
        }
        return allocated;
    }
}
