package xic;

import java.io.*;
import org.apache.commons.io.FilenameUtils;
import lexer.*;
import parser.*;

public class Xic {
    public static void main(String [] args) {
        if (args.length > 1 && args[0].equals("--lex")) {
            try {
                for(int i=1;i<args.length;i++) {
                    String source = args[i];
                    String output = FilenameUtils.removeExtension(source) + ".lexed";
                    BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
                    XiLexer lexer = new XiLexer(new FileReader(source));
                    try {
                        Token t = lexer.nextToken();
                        while (t.type != TokenType.EOF) {
                            w.append(t.toString() + "\n");
                            t = lexer.nextToken();
                        }
                        w.close();
                    } catch (Exception e) {
                        w.append(e.getMessage());
                        w.close();
                        System.out.println(e.getMessage());
                    }
                } 
            } catch (Exception e) {
                System.out.println("Invalid input for --lex. See --help for correct usage.");
            }

        }
        else if(args.length > 1 && args[0].equals("--parse")){
            try {
                for(int i=1;i<args.length;i++) {
                    String source = args[i];
                    String output = FilenameUtils.removeExtension(source) + ".parsed";
                    String ext = FilenameUtils.getExtension(source);
                    BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
                    if (ext.equals("xi")){

                    }
                    else if (ext.equals("ixi")){

                    }
                    else throw new Exception ();
                    
                } 
            } catch (Exception e) {
                System.out.println("Invalid input for --parse. See --help for correct usage.");
            }

        }
        else if(args.length > 1 && ((args[0].equals("--parse") && args[1].equals("--lex") || (args[1].equals("--parse") && args[0].equals("--lex")){
            // try {
            //     for(int i=1;i<args.length;i++) {
            //         String source = args[i];
            //         String output = FilenameUtils.removeExtension(source) + ".parsed";
            //         String ext = FilenameUtils.getExtension(source);
            //         BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
            //         if (ext.equals("xi")){

            //         }
            //         else if (ext.equals("ixi")){

            //         }
            //         else throw new Exception ();
                    
            //     } 
            // } catch (Exception e) {
            //     System.out.println("Invalid input for --parse. See --help for correct usage.");
            // }

        }
        else if (args.length == 1 && args[0].equals("--help") || true) {
          System.out.println("Usage: xic [options] <source-files>");
          System.out.println("  --help:                 Print a synopsis of options");
          System.out.println("  --lex <source-files>:   For each source file filename.xi, generate a lexed file filename.lexed");
          System.out.println("  --parse <source-files>: For each source file filename.xi/filename.ixi, generate a parsed file filename.parsed/filename.iparsed");
        }
        
    }
}
