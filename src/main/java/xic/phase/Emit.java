package xic.phase;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import ast.XiProgram;
import type.GlobalContext;

import emit.ABIContext;
import emit.Canonizer;
import emit.Emitter;

import ir.IRCompUnit;
import ir.Printer;

import optimize.graph.*;

import util.Filename;
import util.Pair;
import util.Result;

import xic.XicException;
import xic.XicInternalException;

public class Emit extends Phase {

    private boolean outputCFG;

    public Emit() { kind = Phase.Kind.EMIT; }

    @Override
    public void setOutputCFG() { outputCFG = true; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;
        
        Pair<XiProgram, GlobalContext> typed = previous.ok().getTyped();
        Pair<IRCompUnit, ABIContext> emitted = Emitter.emitIR(typed.first, typed.second);
        
        if (!(output || outputCFG)) return new Result<>(Product.emitted(emitted));

        String out = Filename.concat(config.sink, config.unit);
        out = Filename.removeExtension(out);
        IRCompUnit canonized = (IRCompUnit) Canonizer.canonize(emitted.first);

        try {
            try {

                if (output) {
                    OutputStream stream = new FileOutputStream(out + "_initial.ir");
                    Printer p = new Printer(stream);
                    canonized.accept(p);
                }
    
                if (outputCFG) {
                    IREdgeFactory<Void> ef = new IREdgeFactory<>();
                    IRGraphFactory<Void> gf = new IRGraphFactory<>(canonized, ef);
                    Map<String, IRGraph<Void>> cfgs = gf.getCfgs();
    
                    for (IRGraph<Void> cfg : cfgs.values()) {
                        cfg.exportCfg(out, "initial");
                    }
                }
    
                return new Result<>(Product.emitted(emitted));

            } catch (XicException e) {
                return new Result<>(e);
            }
        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
