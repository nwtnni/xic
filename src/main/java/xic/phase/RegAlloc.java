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
        CompUnit<Reg> allocated = ColorAllocator.allocate(assembly);

        String out = Filename.concat(config.sink, config.unit);
        out = Filename.setExtension(out, "s");
        Filename.makePathTo(out);

        try {
            FileWriter w = new FileWriter(out);

            for (String i : allocated.toAssembly()) {
                w.append(i + "\n");
            }
            w.close();
        } catch (IOException e) {
        }

        return new Result<>(Product.allocated(allocated));
    }
}
