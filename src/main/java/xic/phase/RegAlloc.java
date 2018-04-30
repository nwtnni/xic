package xic.phase;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import assemble.*;
import assemble.instructions.*;
import optimize.graph.*;
import optimize.register.*;

import util.Result;
import util.*;

public class RegAlloc extends Phase {

    public RegAlloc() { kind = Phase.Kind.REG_ALLOC; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        CompUnit<Temp> assembly = previous.ok().getAssembled();

        // Debug abstract assembly
        String out = Filename.concat(config.sink, config.unit);


        String asa = Filename.setExtension(out, "as.s");
        Filename.makePathTo(out);

        try {
            FileWriter w = new FileWriter(asa);

            for (String i : assembly.toAssembly()) {
                w.append(i + "\n");
            }

            w.close();
        } catch (IOException e) {
        }

        // End debug

        LVEdgeFactory ef = new LVEdgeFactory();
        ASAGraphFactory<Set<Temp>> gf = new ASAGraphFactory<>(ef);
        Map<String, ASAGraph<Set<Temp>>> cfgs = gf.getAllCfgs(assembly);


        // for (ASAGraph<Set<Temp>> cfg : cfgs.values()) {
        //     Pair<Map<Instr<Temp>, Set<Temp>>, Map<Instr<Temp>, Set<Temp>>> init = LVInitVisitor.init(cfg);
        //     Map<Instr<Temp>, Set<Temp>> liveVars = LiveVariableWorklist.computeLiveVariables(cfg, init.first, init.second);
        
        //     System.out.println("\n" + cfg.originalFn.sourceName);
        //     for (Instr<Temp> ins : cfg.originalFn.stmts) {
        //         System.out.println("instr: " + ins);
        //         System.out.println("live " + liveVars.get(ins));
        //         System.out.println("use " + init.first.get(ins));
        //         System.out.println("def " + init.second.get(ins)); 
        //     }
        // }
           


        // TODO: Run analyses and optimizations



        // CompUnit<Reg> allocated = ColorAllocator.allocate(after);

        // out = Filename.concat(config.sink, config.unit);
        // out = Filename.setExtension(out, "s");
        // Filename.makePathTo(out);

        // try {
        //     FileWriter w = new FileWriter(out);

        //     for (String i : allocated.toAssembly()) {
        //         w.append(i + "\n");
        //     }
        //     w.close();
        // } catch (IOException e) {
        // }


        // Convert back to ASA to pass to TrivialAlloc for debug
        CompUnit<Temp> after = new CompUnit<>();
        for (ASAGraph<Set<Temp>> cfg : cfgs.values()) {
            after.fns.add(cfg.toASA());
            try {
                cfg.exportCfg(out, "debug");
            } catch (Exception e) {}
        }
        return new Result<>(Product.assembled(after));
    }
}
