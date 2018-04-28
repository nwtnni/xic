package xic.phase;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import emit.ABIContext;
import emit.ConstantFolder;
import emit.Canonizer;
import ir.IRCompUnit;
import ir.Printer;
import ir.*;

import optimize.graph.IRGraph;
import optimize.graph.IRGraphFactory;
import optimize.graph.IREdgeFactory;
import optimize.propagate.*;

import util.Filename;
import util.Pair;
import util.Result;
import xic.XicException;
import xic.XicInternalException;

public class ConstProp extends Phase {

    private boolean outputCFG;

    public ConstProp() { kind = Phase.Kind.CONST; output = false; }

    @Override
    public void setOutputCFG() { this.outputCFG = true; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        Pair<IRCompUnit, ABIContext> ir = previous.ok().getEmitted();

        // Transform to CFG
        ConstEdgeFactory ef = new ConstEdgeFactory();
        IRGraphFactory<Map<IRTemp, Optional<IRConst>>> gf = new IRGraphFactory<>(ir.first, ef);
        Map<String, IRGraph<Map<IRTemp, Optional<IRConst>>>> cfgs = gf.getCfgs();

        // Run analyses and optimizations
        for(String key: cfgs.keySet()) {
            IRGraph<Map<IRTemp, Optional<IRConst>>> cfg = cfgs.get(key);

            Map<IRStmt, Map<IRTemp, Optional<IRConst>>> consts = ConstWorklist.computeAvailableConsts(cfg);
            
            System.out.println(key);
            for (IRStmt s : ir.first.getFunction(key).body().stmts()) {
                System.out.println(consts.get(s));
                System.out.println(s);
            }
            
        }

        // Convert back to IR
        IRCompUnit after = new IRCompUnit(ir.first.name());
        for (IRGraph<?> cfg : cfgs.values()) {
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
                    OutputStream stream = new FileOutputStream(out + "_cp.ir");
                    Printer p = new Printer(stream);
                    after.accept(p);
                }
    
                if (outputCFG) {
                    for (IRGraph<?> cfg : cfgs.values()) {
                        cfg.exportCfg(out, "cp");
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
