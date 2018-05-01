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

public class Irgen extends Phase {

    private boolean outputCFG;

    public Irgen() { kind = Phase.Kind.IRGEN; output = false; }

    @Override
    public void setOutputCFG() { this.outputCFG = true; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        Pair<IRCompUnit, ABIContext> ir = previous.ok().getEmitted();

        // Skip output if no flags specified
        if (!(output || outputCFG)) {
            return previous;
        }

        String out = Filename.concat(config.sink, config.unit);
        out = Filename.removeExtension(out);

        try {
            try {
                // Generate final ir if flag passed
                if (output) {
                    OutputStream stream = new FileOutputStream(out + "_final.ir");
                    Printer p = new Printer(stream);
                    ir.first.accept(p);
                }
    
                // Generate final cfg dot files if flag passed
                if (outputCFG) {
                    IREdgeFactory<Void> ef = new IREdgeFactory<>();
                    IRGraphFactory<Void> gf = new IRGraphFactory<>(ir.first, ef);
                    Map<String, IRGraph<Void>> cfgs = gf.getCfgs();
                    for (IRGraph<Void> cfg : cfgs.values()) {
                        cfg.exportCfg(out, "final");
                    }
                }
                
                return previous;
            } catch (XicException e) {
                return new Result<>(e);
            }
        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
