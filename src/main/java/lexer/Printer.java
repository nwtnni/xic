package lexer;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.io.FilenameUtils;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import parser.XiSymbol;

public class Printer {

    // TODO: Throw XicException
	public static void print(String source, String sink, String unit) {
		XiLexer lexer = XiLexer.from(source, unit);

        String lexed = FilenameUtils.removeExtension(unit) + ".lexed";
        String output = FilenameUtils.concat(sink, lexed);

        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter(new FileWriter(output, false));
        } catch (Exception e) {
            System.out.println(e.toString());
            return;
        }

        try {
            ComplexSymbol s = (ComplexSymbol) lexer.next_token();
            while (s.sym != XiSymbol.EOF) {
                writer.append(format(s) + "\n");
                s = (ComplexSymbol) lexer.next_token();
            }
            writer.close();
        } catch (Exception e) {
        	try {
                if (writer != null) {
                    writer.append(e.toString());
                    writer.close();
               }
        	} catch (Exception io) {}

            System.out.println(e.toString());
            return;
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
