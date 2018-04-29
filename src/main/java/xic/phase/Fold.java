package xic.phase;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import emit.ABIContext;
import emit.ConstantFolder;
import emit.Canonizer;
import ir.*;

import optimize.graph.*;

import util.Filename;
import util.Pair;
import util.Result;
import xic.XicException;
import xic.XicInternalException;

/**
 * Performs constant folding on the IR.
 * This phase also lowers the IR to canonical form in the process of constant folding.
 */
public class Fold extends Phase {

    protected boolean outputCFG;

    public Fold() { kind = Phase.Kind.FOLD; }

    @Override
    public void setOutputCFG() { this.outputCFG = true; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        // Run lowering and constant folding
        Pair<IRCompUnit, ABIContext> ir = previous.ok().getEmitted();
        ConstantFolder.constantFold(ir.first);
        IRCompUnit canonized = (IRCompUnit) Canonizer.canonize(ir.first);
        ConstantFolder.constantFold(canonized);

        // Transform to CFG and convert back to eliminate unneccesary jumps and labels
        IREdgeFactory<Map<IRExpr, IRStmt>> ef = new IREdgeFactory<>();
        IRGraphFactory<Map<IRExpr, IRStmt>> gf = new IRGraphFactory<>(canonized, ef);
        Map<String, IRGraph<Map<IRExpr, IRStmt>>> cfgs = gf.getCfgs();
        canonized = new IRCompUnit(ir.first.name());
        for (IRGraph<Map<IRExpr, IRStmt>> cfg : cfgs.values()) {
            canonized.appendFunc(cfg.toIR());
        }


        String out = Filename.concat(config.sink, config.unit);
        out = Filename.removeExtension(out);

        try {
            try {
                if (output) {
                    OutputStream stream = new FileOutputStream(out + "_cf.ir");
                    Printer p = new Printer(stream);
                    canonized.accept(p);
                }
    
                if (outputCFG) {
                    IREdgeFactory<Void> ef2 = new IREdgeFactory<>();
                    IRGraphFactory<Void> gf2 = new IRGraphFactory<>(canonized, ef2);
                    Map<String, IRGraph<Void>> cfgs2 = gf2.getCfgs();
    
                    for (IRGraph<Void> cfg : cfgs2.values()) {
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
