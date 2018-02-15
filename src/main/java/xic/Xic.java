package xic;

import java.io.*;
import org.apache.commons.io.FilenameUtils;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;

import parser.XiSymbol;
import lexer.*;
import parser.*;

public class Xic {
    // public static void main(String [] args) {
    //     if (args.length > 1 && args[0].equals("--lex")) {
    //         try {
    //             for(int i = 1; i < args.length; i++) {
    //                 lex(args[i]);
    //             } 
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //             System.out.println("Invalid input for --lex. See --help for correct usage.");
    //         }
    //     }
    //     else if(args.length > 1 && args[0].equals("--parse")){
    //         try {
    //             for(int i=1;i<args.length;i++) {
    //                 String source = args[i];
                    
    //                 String ext = FilenameUtils.getExtension(source);
    //                 BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
    //                 if (ext.equals("xi")){
    //                     String output = FilenameUtils.removeExtension(source) + ".parsed";
    //                 }
    //                 else if (ext.equals("ixi")){

    //                 }
    //                 else throw new Exception ();
                    
    //             } 
    //         } catch (Exception e) {
    //             System.out.println("Invalid input for --parse. See --help for correct usage.");
    //         }

    //     }
    //     else if (args.length == 1 && args[0].equals("--help") || true) {
    //       System.out.println("Usage: xic [options] <source-files>");
    //       System.out.println("  --help:                 Print a synopsis of options");
    //       System.out.println("  --lex <source-files>:   For each source file filename.xi, generate a lexed file filename.lexed");
    //       System.out.println("  --parse <source-files>: For each source file filename.xi/filename.ixi, generate a parsed file filename.parsed/filename.iparsed");
    //     }
    // }


    public static void main(String [] args) {
        boolean lexFlag = false;
        boolean parseFlag = false;
        boolean helpFlag = false;

        for (int i = 0; i < args.length; i++){
            if (args[i].equals("--lex")){
                lexFlag = true;
            }
            else if (args[i].equals("--parse")){
                parseFlag = true;
            }
            else if (args[i].equals("--help")){
                helpFlag = true;
            }
        }

        if (helpFlag == true || (lexFlag == false && parseFlag == false)){
            System.out.println("Usage: xic [options] <source-files>");
            System.out.println("  --help:                 Print a synopsis of options");
            System.out.println("  --lex <source-files>:   For each source file filename.xi, generate a lexed file filename.lexed");
            System.out.println("  --parse <source-files>: For each source file filename.xi/filename.ixi, generate a parsed file filename.parsed/filename.iparsed");
        }
        else {
            if (lexFlag == true){
                lex(source);
            }
            if (parseFlag == true){
                parse(source);
            }
        }
    }

    private static void lex(String source) throws IOException {
        String output = FilenameUtils.removeExtension(source) + ".lexed";
        BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
        XiLexer lexer = new XiLexer(new FileReader(source));
        lexer.init(source, new ComplexSymbolFactory());
        try {
           ComplexSymbol s = (ComplexSymbol) lexer.next_token();
            while (s.sym != XiSymbol.EOF) {
                w.append(formatSymbol(s) + "\n");
                s = (ComplexSymbol) lexer.next_token();
            }
            w.close();
        } catch (Exception e) {
            w.append(e.getMessage());
            w.close();
            System.out.println(e.getMessage());
        }
    }

    private static String formatSymbol(ComplexSymbol s) {
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

    private static void parse(String source) throws IOException {
        String ext = FilenameUtils.getExtension(source);
        String output = FilenameUtils.removeExtension(source) + ".parsed";
        OutputStream stream = new FileOutputStream(output);
        if (ext.equals("xi")){
            /* add the pretty print */

        }
        else if (ext.equals("ixi")){
            output = FilenameUtils.removeExtension(source) + ".iparsed";
        }
        else throw new Exception();
        
    }
}
