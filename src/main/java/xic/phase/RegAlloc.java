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

        CompUnit assembly = previous.ok().getAssembled();
        
        // Debug
        String out = Filename.concat(config.sink, config.unit);
        out = Filename.setExtension(out, "as.s");
        Filename.makePathTo(out);
        
        try {
            FileWriter w = new FileWriter(out);
            
            for (String i : assembly.toAbstractAssembly()) {
                w.append(i + "\n");
            }

            w.close();
        } catch (IOException e) {
        }

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
        
        // Debug LV
        out = Filename.setExtension(out, "lv.s");
        try {
            FileWriter lvw = new FileWriter(out); 
            for (FuncDecl fn : after.fns) {

                lvw.append(fn.sourceName + "\n");
                for (Instr i : fn.stmts) {
                    lvw.append(i + ": \n");
                    lvw.append("in: " + i.in + "\n");
                    lvw.append("use: " + i.use + "\n");
                    lvw.append("def: " + i.def + "\n");
                    lvw.append("out: " + i.out + "\n");
                    lvw.append("\n");
                }
            
            }
            lvw.close();
        } catch (IOException e) {
        }


        return new Result<>(Product.assembled(after));
    }
}
