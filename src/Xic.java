import java.io.*;
import lexer.*;

public class Xic {
    public static void main(String [] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                XiLexer lexer = new XiLexer(new FileReader(args[i]));
                Token t = lexer.nextToken();
                while (t.type != TokenType.EOF) {
                    System.out.println(t.toString());
                    t = lexer.nextToken();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
