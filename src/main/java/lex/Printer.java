package lex;

import java.io.*;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import parse.XiSymbol;
import util.Filename;
import xic.XicException;

/**
 * Convenience class to write the result of a lexing run to file.
 */
public class Printer {

    /**
     * Lexes the given file, and outputs diagnostic
     * information to the given output file.
     * 
     * @param source Directory to search for the source
     * @param sink Directory to output the result
      * @param unit Path to the target source file, relative to source
     * @throws XicException if the Printer was unable to write to the given file
     */
    public static void print(String source, String sink, String unit) throws XicException {
        String lexed = Filename.setExtension(unit, "lexed");
        lexed = Filename.concat(sink, lexed);
        try {
            Filename.makePathTo(lexed);
            BufferedWriter writer = new BufferedWriter(new FileWriter(lexed, false));
            try {
                XiLexer lexer = XiLexer.from(source, unit);

                ComplexSymbol s = (ComplexSymbol) lexer.nextToken();
                while (s.sym != XiSymbol.EOF) {
                    writer.append(format(s) + "\n");
                    s = (ComplexSymbol) lexer.nextToken();
                }
                
                writer.close();
            } catch (LexException e) {
                writer.append(e.toWrite());
                writer.close();
                throw e;
            }
        } catch (IOException io) {
            throw XicException.write(lexed);
        }
    }

    private static String format(ComplexSymbol s) {
        String label;
        switch (s.sym) {
            case XiSymbol.IDENTIFIER:
                label = "id ";
                break;
            case XiSymbol.INTEGER:
                label = "integer ";
                break;
            case XiSymbol.CHAR:
                label = "character ";
                break;
            case XiSymbol.STRING:
                label = "string ";
                break;
            default:
                label = "";
        }
        Location l = s.getLeft();
        return l.getLine() + ":" + l.getColumn() + " " + label + s.getName();
    }
}
