package xic.phase;

import java.util.Map;
import java.util.Set;

import assemble.*;
import optimize.graph.*;
import optimize.register.*;
import util.Result;

public class RegAlloc extends Phase {

    public RegAlloc() { kind = Phase.Kind.REG_ALLOC; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous; 

        CompUnit assembly = previous.ok().getAssembled(); 

        LVEdgeFactory ef = new LVEdgeFactory();
        ASAGraphFactory<Set<Temp>> gf = new ASAGraphFactory<>(assembly, ef);
        Map<String, ASAGraph<Set<Temp>>> cfgs = gf.getCfgs();

        // Run analyses and optimizations
        for(ASAGraph<Set<Temp>> cfg : cfgs.values()) {
            LiveVariableWorklist lv = new LiveVariableWorklist(cfg);
            lv.doWorklist();
        }

        // Convert back to IR
        CompUnit after = new CompUnit();
        for (ASAGraph<Set<Temp>> cfg : cfgs.values()) {
            after.fns.add(cfg.toASA());
        }
        
        return new Result<>(Product.assembled(after));
    }
}
