package type;

import java.io.FileWriter;
import java.io.IOException;

import ast.Node;
import parse.XiParser;
import util.Filename;
import xic.XicException;

/**
 * Convenience class to write the result of a type check run to file.
 */
public class Printer {
    
    /**
     * Parses the given file, runs the type checker, and outputs diagnostic
     * information to the given output file.
     * 
     * @param source Directory to search for the source
     * @param sink Directory to output the result
     * @param lib Directory to search for interface files
      * @param unit Path to the target source file, relative to source
     * @throws XicException if the Printer was unable to write to the given file
     */
    public static void print(String source, String sink, String lib, String unit) throws XicException {
        String ext = Filename.getExtension(unit);
        String output = Filename.concat(sink, Filename.removeExtension(unit));
        output = Filename.setExtension(output, "typed");

        FileWriter writer = null;
        try {
            Filename.makePathTo(output);
            writer = new FileWriter(output);
            try {
                switch (ext) {
                    case "xi":
                        Node ast = XiParser.from(source, unit);
                        TypeChecker.check(lib, ast);
                        break;
                    default:
                        throw XicException.unsupported(unit);
                }
        
                writer.write("Valid Xi Program");
                writer.close();
            } catch (XicException xic) {
                writer.write(xic.toWrite());
                writer.close();
                throw xic;
            }
        } catch (IOException io) {
            throw XicException.write(output);
        }
    }
}