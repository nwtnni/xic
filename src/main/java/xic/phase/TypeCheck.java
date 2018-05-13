package xic.phase;

import java.io.IOException;
import java.io.FileWriter;

import ast.XiProgram;
import type.TypeChecker;
import type.FnContext;

import util.Filename;
import util.Pair;
import util.Result;

import xic.XicException;
import xic.XicInternalException;

public class TypeCheck extends Phase {

    public TypeCheck() { kind = Phase.Kind.TYPE; }

    @Override
    public Result<Product> process(Config config, Result<Product> previous) {

        String ext = Filename.getExtension(config.unit);
        String out = Filename.concat(config.sink, config.unit);
        out = Filename.setExtension(out, "typed");

        try {
            try {

                if (previous.isErr()) throw previous.err();

                if (!ext.equals("xi")) throw XicException.unsupported(config.unit);

                XiProgram ast = previous.ok().getParsed();

                FnContext context = TypeChecker.check(config.lib, ast);

                if (output) {
                    Filename.makePathTo(out);
                    FileWriter writer = new FileWriter(out);
                    writer.write("Valid Xi Program");
                    writer.close();
                }

                return new Result<>(Product.typed(new Pair<>(ast, context)));

            } catch (XicException e) {

                if (output) {
                    Filename.makePathTo(out);
                    FileWriter writer = new FileWriter(out);
                    writer.write(e.toWrite());
                    writer.close();
                }

                return new Result<>(e);
            }

        } catch (IOException e) {
            throw new XicInternalException(e.toString());
        }
    }
}
