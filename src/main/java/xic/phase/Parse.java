package xic.phase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ast.Program;
import parse.XiParser;
import parse.IXiParser;
import parse.Printer;

import util.Result;
import util.Filename;
import xic.XicException;
import xic.XicInternalException;

public class Parse extends Phase {

    public Parse() { kind = Phase.Kind.PARSE; }
    
    @Override
    public Result<Intermediate> process(Config config, Result<Intermediate> previous) {

        try {

            String ext = Filename.getExtension(config.unit);
            String out = Filename.concat(config.sink, config.unit);

            try {

                if (previous.isErr()) throw previous.err();
                
                Program ast = null;
                switch (ext) {
                    case "xi":
                        out = Filename.setExtension(out, "parsed");
                        ast = (Program) XiParser.from(config.source, config.unit);
                        break;
                    case "ixi":
                        out = Filename.setExtension(out, "iparsed");
                        ast = (Program) IXiParser.from(config.source, config.unit);
                        break;
                    default:
                        throw XicException.unsupported(config.unit);
                }

                if (output) {
                    Filename.makePathTo(out);
                    OutputStream stream = new FileOutputStream(out);
                    Printer printer = new Printer(stream);
                    ast.accept(printer);
                }

                return new Result<>(new Intermediate(ast));

            } catch (XicException e) {
                if (output) {
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
