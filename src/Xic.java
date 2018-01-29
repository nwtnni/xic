import java.io.*;
import lexer.XiLexer;
import lexer.XiLexer.TokenType;

public class Xic {
    public static void main(String [] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                XiLexer lexer = new XiLexer(new FileReader(args[i]));
                XiLexer.Token t;
                do {
                    t = lexer.nextToken();
                } while (t.type != TokenType.EOF);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
