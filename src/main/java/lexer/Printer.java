package lexer;

import java.io.*;
import xic.FilenameUtils;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import parser.XiSymbol;
import xic.XicException;

public class Printer {

	public static void print(String source, String sink, String unit) throws XicException {
        String lexed = FilenameUtils.setExtension(unit, "lexed");
        lexed = FilenameUtils.concat(sink, lexed);
        try {
            FilenameUtils.makePathTo(lexed);
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
            io.printStackTrace();
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
