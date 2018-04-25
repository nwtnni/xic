package xic.phase;

import java.io.IOException;
import java.io.FileWriter;

import ast.Program;
import type.TypeChecker;

import util.Filename;
import util.Result;

import xic.XicException;
import xic.XicInternalException;

public class Type extends Phase {

    public Type() { kind = Phase.Kind.TYPE; }

    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {

        String ext = Filename.getExtension(config.unit);
        String out = Filename.concat(config.sink, config.unit);
        out = Filename.setExtension(out, "typed");

        try {
            try {

                if (previous.isErr()) throw previous.err();

                if (!ext.equals("xi")) throw XicException.unsupported(config.unit);

                Program ast = previous.ok().getAST();

                TypeChecker.check(config.lib, ast);

                if (output) {
                    Filename.makePathTo(out);
                    FileWriter writer = new FileWriter(out);
                    writer.write("Valid Xi Program");
                    writer.close();
                }

                return new Result<>(new Intermediate(ast));

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
