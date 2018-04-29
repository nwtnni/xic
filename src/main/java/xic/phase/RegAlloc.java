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
import util.Filename;

public class RegAlloc extends Phase {

    public RegAlloc() { kind = Phase.Kind.REG_ALLOC; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        CompUnit<Temp> assembly = previous.ok().getAssembled();

        // Debug abstract assembly
        String out = Filename.concat(config.sink, config.unit);
        out = Filename.setExtension(out, "as.s");
        Filename.makePathTo(out);

        try {
            FileWriter w = new FileWriter(out);

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

        // Run analyses and optimizations

        // TODO: move this stuff to the allocator


        out = out.substring(0, out.length() - 4);

        for(ASAGraph<Set<Temp>> cfg : cfgs.values()) {
            Map<Instr<Temp>, Set<Temp>> lv = LiveVariableWorklist.computeLiveVariables(cfg);

            String lvOut = out + cfg.originalFn.sourceName + ".lv.s";

            // Debug LV
            try {
                FileWriter lvw = new FileWriter(lvOut);

                FuncDecl<Temp> fn = cfg.toASA();

                lvw.append(fn.sourceName + "\n");
                for (Instr<Temp> i : fn.stmts) {
                    lvw.append(i + ": \n");
                    lvw.append("live: " + lv.get(i) + "\n");
                    lvw.append("\n");
                }

                lvw.close();
            } catch (IOException e) {
            }

        }

        // Convert back to ASA
        CompUnit<Temp> after = new CompUnit<>();
        for (ASAGraph<Set<Temp>> cfg : cfgs.values()) {
            after.fns.add(cfg.toASA());
            try {
                cfg.exportCfg(out, "debug");
            } catch (Exception e) {}
        }

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

        return new Result<>(Product.assembled(after));
    }
}
