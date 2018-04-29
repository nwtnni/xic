package xic.phase;

import java.io.IOException;
import java.io.FileWriter;

import assemble.CompUnit;
import assemble.Temp;
import assemble.Reg;
import assemble.TrivialAllocator;

import util.Filename;
import util.Pair;
import util.Result;

import xic.XicException;
import xic.XicInternalException;

public class TrivialAlloc extends Phase {

    public TrivialAlloc() { kind = Phase.Kind.ALLOCATE; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        try {

            if (previous.isErr()) return previous;

            CompUnit<Temp> tiled = previous.ok().getAssembled();
            CompUnit<Reg> allocated = TrivialAllocator.allocate(tiled);


            // Debug abstract assembly
            String outAbs = Filename.concat(config.sink, config.unit);
            outAbs = Filename.setExtension(outAbs, "as.s");
            Filename.makePathTo(outAbs);

            try {
                FileWriter w = new FileWriter(outAbs);

                for (String i : tiled.toAssembly()) {
                    w.append(i + "\n");
                }

                w.close();
            } catch (IOException e) {
            }

            // End debug

            String out = Filename.concat(config.sink, config.unit);
            out = Filename.setExtension(out, "s");

            Filename.makePathTo(out);
            FileWriter w = new FileWriter(out);
            
            for (String i : allocated.toAssembly()) {
                w.append(i + "\n");
            }

            w.close();

            return new Result<>(Product.allocated(allocated));

        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
