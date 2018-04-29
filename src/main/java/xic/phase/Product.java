package xic.phase;

import assemble.CompUnit;
import assemble.Reg;
import assemble.Temp;
import ast.Program;
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
    private final CompUnit<Temp> assembled;
    private final CompUnit<Reg> allocated;

    private Product (XiLexer l, Program p, Pair<Program, FnContext> t, Pair<IRCompUnit, ABIContext> e, CompUnit<Temp> a, CompUnit<Reg> r) {
        lexed = l; 
        parsed = p;
        typed = t;
        emitted = e;
        assembled = a;
        allocated = r;
    }

    public static Product empty() { return new Product(null, null, null, null, null, null); }

    public static Product lexed(XiLexer l) { return new Product(l, null, null, null, null, null); }

    public static Product parsed(Program p) { return new Product(null, p, null, null, null, null); }

    public static Product typed(Pair<Program, FnContext> t) { return new Product(null, null, t, null, null, null); }

    public static Product emitted(Pair<IRCompUnit, ABIContext> e) { return new Product(null, null, null, e, null, null); }

    public static Product assembled(CompUnit<Temp> a) { return new Product(null, null, null, null, a, null); }

    public static Product allocated(CompUnit<Reg> a) { return new Product(null, null, null, null, null, a); }

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
