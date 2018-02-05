package xic;

import java.io.*;
import org.apache.commons.io.FilenameUtils;
import lexer.*;

public class Xic {
    public static void main(String [] args) {
        if (args.length > 1 && args[0].equals("--lex")) {
            try {
                for(int i=1;i<args.length;i++) {
                    String source = args[i];
                    String output = FilenameUtils.removeExtension(source) + ".lexed";
                    BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
                    try {
                        XiLexer lexer = new XiLexer(new FileReader(source));
                        Token t = lexer.nextToken();
                        while (t.type != TokenType.EOF) {
                            w.append(t.toString() + "\n");
                            t = lexer.nextToken();
                        }
                        w.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        w.append(e.getMessage());
                        w.close();
                        System.out.println(e.getMessage());
                    }
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (args.length == 1 && args[0].equals("--help") || true) {
            try {
                InputStream stream = Xic.class.getClassLoader().getResourceAsStream("resources/help.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String line = in.readLine();
                while(line != null)
                {
                  System.out.println(line);
                  line = in.readLine();
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
