package xic.phase;

import java.io.*;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;

import lex.XiLexer;
import parse.XiSymbol;
import util.Result;
import util.Filename;
import xic.XicException;
import xic.XicInternalException;

public class Lex extends Phase {

    public Lex() { kind = Phase.Kind.LEX; }

    @Override
    public Result<Intermediate> process(Result<Intermediate> previous) {

        // First phase; must be ok
        Intermediate files = previous.ok();

        // Write to file
        if (output) {

            String lexed = Filename.setExtension(files.unit, "lexed");
            lexed = Filename.concat(files.sink, lexed);

            try { 

                Filename.makePathTo(lexed);
                BufferedWriter writer = new BufferedWriter(new FileWriter(lexed, false));

                try {
                    XiLexer lexer = XiLexer.from(files.source, files.unit);

                    for (
                        ComplexSymbol s = (ComplexSymbol) lexer.nextToken();
                        s.sym != XiSymbol.EOF;
                        s = (ComplexSymbol) lexer.nextToken()
                    ) writer.append(format(s) + "\n");
                    
                    writer.close();

                } catch (XicException e) {
                    writer.append(e.toWrite());
                    writer.close();
                    return new Result<>(e);
                }
            } catch (IOException e) {
                throw new XicInternalException(e.toString());
            }
        }
        
        // Return a fresh lexer for the next phase
        try {
            return new Result<>(
                new Intermediate(
                    files,
                    XiLexer.from(files.source, files.unit)
                )
            );
        } catch (XicException e) {
            return new Result<>(e);
        }
    }

    /**
     * Converts tokens to pretty-printed strings.
     */
    private String format(ComplexSymbol s) {
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
