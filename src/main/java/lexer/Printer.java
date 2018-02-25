package lexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import parser.XiSymbol;
import xic.XicException;

public class Printer {

	public static void print(String source, String sink, String unit) throws XicException {
		XiLexer lexer = XiLexer.from(source, unit);

        String lexed = FilenameUtils.removeExtension(unit) + ".lexed";
        String output = FilenameUtils.concat(sink, lexed);
        BufferedWriter writer = null;
        
        try {
        	try {
	            writer = new BufferedWriter(new FileWriter(output, false));
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
        	throw XicException.write(output);
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
