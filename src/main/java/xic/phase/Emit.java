package xic.phase;

import ast.Program;
import type.FnContext;

import emit.ABIContext;
import emit.Emitter;
import ir.IRCompUnit;

import util.Filename;
import util.Pair;
import util.Result;

import xic.XicException;

public class Emit extends Phase {

    public Emit() { kind = Phase.Kind.EMIT; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;
        
        Pair<Program, FnContext> typed = previous.ok().getTyped();

        try {
            Pair<IRCompUnit, ABIContext> result = Emitter.emitIR(typed.first, typed.second);
            return new Result<>(Product.emitted(result));
        } catch (XicException e) {
            return new Result<>(e);
        }
    }
}
