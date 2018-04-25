package xic.phase;

import java.io.IOException;
import java.io.FileWriter;

import assemble.CompUnit;
import assemble.TrivialAllocator;

import util.Filename;
import util.Pair;
import util.Result;

import xic.XicException;
import xic.XicInternalException;

public class Allocate extends Phase {

    public Allocate() { kind = Phase.Kind.ALLOCATE; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        try {

            if (previous.isErr()) return previous;

            CompUnit tiled = previous.ok().getAssembled();
            tiled = TrivialAllocator.allocate(tiled);

            String out = Filename.concat(config.sink, config.unit);
            out = Filename.setExtension(out, "s");

            Filename.makePathTo(out);
            FileWriter w = new FileWriter(out);
            
            for (String i : tiled.toAssembly()) {
                w.append(i + "\n");
            }

            w.close();

            return new Result<>(Product.assembled(tiled));

        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
