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

import optimize.graph.*;

import util.Filename;
import util.Pair;
import util.Result;
import xic.XicException;
import xic.XicInternalException;

public class Fold extends Phase {

    private boolean outputCFG;

    public Fold() { kind = Phase.Kind.FOLD; }

    @Override
    public void setOutputCFG() { this.outputCFG = true; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        Pair<IRCompUnit, ABIContext> ir = previous.ok().getEmitted();

        if (!(output || outputCFG)) {
            ConstantFolder.constantFold(ir.first);
            return new Result<>(Product.emitted(ir));
        }

        String out = Filename.concat(config.sink, config.unit);
        out = Filename.removeExtension(out);

        // TODO: figure out a better phase ordering so this isn't necessary?
        IRCompUnit canonized = (IRCompUnit) Canonizer.canonize(ir.first);
        ConstantFolder.constantFold(canonized);

        try {
            try {
                if (output) {
                    OutputStream stream = new FileOutputStream(out + "_cf.ir");
                    Printer p = new Printer(stream);
                    canonized.accept(p);
                }
    
                if (outputCFG) {
                    IREdgeFactory<Void> ef = new IREdgeFactory<>();
                    IRGraphFactory<Void> gf = new IRGraphFactory<>(canonized, ef);
                    Map<String, IRGraph<Void>> cfgs = gf.getCfgs();
    
                    for (IRGraph<Void> cfg : cfgs.values()) {
                        cfg.exportCfg(out, "cf");
                    }
                }
                
                // Returns an already canonized IR AST: redundant work for next stage
                return new Result<>(Product.emitted(new Pair<>(canonized, ir.second)));
            } catch (XicException e) {
                return new Result<>(e);
            }
        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
