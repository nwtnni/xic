package xic.phase;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import emit.ABIContext;
import emit.ConstantFolder;
import emit.Canonizer;
import ir.IRCompUnit;
import ir.Printer;

import optimize.IRGraph;
import optimize.IRGraphFactory;
import optimize.IREdgeFactory;

import util.Filename;
import util.Pair;
import util.Result;
import xic.XicException;
import xic.XicInternalException;

public class Cse extends Phase {

    private boolean outputCFG;

    public Cse() { kind = Phase.Kind.CSE; output = false; }

    @Override
    public void setOutputCFG() { this.outputCFG = true; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        Pair<IRCompUnit, ABIContext> ir = previous.ok().getEmitted();

        // Transform to CFG
        IREdgeFactory<Void> ef = new IREdgeFactory<>();
        IRGraphFactory<Void> gf = new IRGraphFactory<>(ir.first, ef);
        Map<String, IRGraph<Void>> cfgs = gf.getCfgs();

        // TODO: Run analyses and optimizations

        // Convert back to IR
        IRCompUnit after = new IRCompUnit(ir.first.name());
        for (IRGraph<Void> cfg : cfgs.values()) {
            after.appendFunc(cfg.toIR());
        }

        // Skip output if no flags specified
        if (!(output || outputCFG)) {
            return new Result<>(Product.emitted(new Pair<>(after, ir.second)));
        }

        String out = Filename.concat(config.sink, config.unit);
        out = Filename.removeExtension(out);

        try {
            try {
                if (output) {
                    OutputStream stream = new FileOutputStream(out + "_cse.ir");
                    Printer p = new Printer(stream);
                    after.accept(p);
                }
    
                if (outputCFG) {
                    for (IRGraph<Void> cfg : cfgs.values()) {
                        cfg.exportCfg(out, "cse");
                    }
                }
                
                return new Result<>(Product.emitted(new Pair<>(after, ir.second)));
            } catch (XicException e) {
                return new Result<>(e);
            }
        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
