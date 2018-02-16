package xic;

import java.io.*;
import java.io.FileInputStream;
import org.apache.commons.io.FilenameUtils;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;

import parser.XiSymbol;
import lexer.*;
import parser.*;
import ast.*;

import java.util.*;

public class Xic {

    public static void main(String [] args) {
        boolean lexFlag = false;
        boolean parseFlag = false;
        boolean helpFlag = false;
        boolean sourceFlag = false;
        String sourcePath = "";
        boolean dFlag = false;
        String dPath = "";

        ArrayList<String> sourceFiles = new ArrayList<String>();
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
            else if (args[i].equals("-sourcepath") && i+1<args.length){
                sourceFlag = true;
                sourcePath = args[++i];
            }
            else if (args[i].equals("-D") && i+1<args.length){
                dFlag = true;
                dPath = args[++i];
            }
            else {
                sourceFiles.add(args[i]);
            }
        }

        if (helpFlag == true || (lexFlag == false && parseFlag == false)) {
            displayHelp();
        }
        else {
            try {
                if (lexFlag == true){
                    for (String source : sourceFiles){
                        lex(source, sourcePath, dPath);
                    }
                
                }
                if (parseFlag == true){
                    for (String source : sourceFiles){
                        parse(source, sourcePath, dPath);
                    }
                }
            } catch (IOException e){
                System.out.println("Could not find file");
            }
        }
    }

    private static void lex(String source, String sourceDir, String outputDir) throws IOException {
        String output = FilenameUtils.concat(outputDir, FilenameUtils.removeExtension(source) + ".lexed");
        
        source = FilenameUtils.concat(sourceDir, source);
        
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
            w.append(e.toString());
            w.close();
            System.out.println(e.toString());
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

    private static void parse(String source, String sourceDir, String outputDir) throws IOException {
        String ext = FilenameUtils.getExtension(source);
        String output = FilenameUtils.concat(outputDir, FilenameUtils.removeExtension(source));
        if (ext.equals("ixi")){
            output = output + ".iparsed";
        } else {
            output = output + ".parsed";
        }

        source = FilenameUtils.concat(sourceDir, source);
          
        try {
            if (ext.equals("xi")){
                OutputStream stream = new FileOutputStream(output);
                XiLexer lexer = new XiLexer(new FileReader(source));
                ComplexSymbolFactory sf = new ComplexSymbolFactory();
                lexer.init(source, sf);
                XiParser parser = new XiParser(lexer, sf);
                
                Printer pp = new Printer(stream);
                Node ast = parser.parse().value();
                Invariant.check(ast);
                pp.print(ast);
            }
            else if (ext.equals("ixi")){
                OutputStream stream = new FileOutputStream(output);
                IXiLexer lexer = new IXiLexer(new FileReader(source));
                ComplexSymbolFactory sf = new ComplexSymbolFactory();
                lexer.init(source, sf);
                IXiParser parser = new IXiParser(lexer, sf);

                Printer pp = new Printer(stream);
                Node ast = parser.parse().value();
                Invariant.check(ast);
                pp.print(ast);
            }
            else {
                displayHelp();
            }
        } catch (Exception e){
            BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
            w.append(e.toString());
            w.close();
            System.out.println(e.toString());

        }
    }

    private static void displayHelp() {
        System.out.println("Usage: xic [options] <source-files>");
        System.out.println("  --help:                 Print a synopsis of options");
        System.out.println("  --lex <source-files>:   For each source file filename.xi, generate a lexed file filename.lexed");
        System.out.println("  --parse <source-files>: For each source file filename.xi/filename.ixi, generate a parsed file filename.parsed/filename.iparsed");
    }
}
