package xic.phase;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;

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

        String out = Filename.concat(config.sink, config.unit);
        out = Filename.setExtension(out, "ir");

        try {
            
            try {

                if (previous.isErr()) throw previous.err();

                Pair<IRCompUnit, ABIContext> emitted = previous.ok().getEmitted();

                IRCompUnit canonized = (IRCompUnit) Canonizer.canonize(emitted.first);

                if (output) {
                    Filename.makePathTo(out);
                    OutputStream stream = new FileOutputStream(out);
                    Printer p = new Printer(stream);
                    canonized.accept(p);
                }

                return new Result<>(Product.emitted(new Pair<>(canonized, emitted.second)));

            } catch (XicException e) {

                if (output) {
                    Filename.makePathTo(out);
                    BufferedWriter w = new BufferedWriter(new FileWriter(out));
                    w.write(e.toWrite());
                    w.close();
                }

                return new Result<>(e);
            }
        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
