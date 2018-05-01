package xic.phase;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.util.Map;

import ir.IRCompUnit;
import ir.Printer;
import emit.ABIContext;
import emit.Canonizer;

import util.Filename;
import util.Pair;
import util.Result;

import xic.XicException;
import xic.XicInternalException;

public class Canonize extends Phase {

    public Canonize() { kind = Phase.Kind.CANONIZE; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        if (previous.isErr()) return previous;

        try {

            // Run lowering
            Pair<IRCompUnit, ABIContext> emitted = previous.ok().getEmitted();
            IRCompUnit canonized = (IRCompUnit) Canonizer.canonize(emitted.first);

            // Generate output for irrun and irgen
            if (output) {
                String out = Filename.concat(config.sink, config.unit);
                out = Filename.setExtension(out, "ir");
                Filename.makePathTo(out);
                OutputStream stream = new FileOutputStream(out);
                Printer p = new Printer(stream);
                canonized.accept(p);
            }

            return new Result<>(Product.emitted(new Pair<>(canonized, emitted.second)));

        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
