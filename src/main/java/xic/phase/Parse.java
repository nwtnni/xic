package xic.phase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ast.XiProgram;
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
    public Result<Product> process(Config config, Result<Product> previous) {

        try {

            String ext = Filename.getExtension(config.unit);
            String out = Filename.concat(config.sink, config.unit);

            try {

                if (previous.isErr()) throw previous.err();
                
                XiProgram ast = null;
                switch (ext) {
                    case "xi":
                        out = Filename.setExtension(out, "parsed");
                        ast = (XiProgram) XiParser.from(config.source, config.unit);
                        break;
                    case "ixi":
                        out = Filename.setExtension(out, "iparsed");
                        ast = (XiProgram) IXiParser.from(config.source, config.unit);
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

                return new Result<>(Product.parsed(ast));

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
