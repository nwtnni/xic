package xic;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import ast.Invariant;
import ast.Node;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import lexer.IXiLexer;
import lexer.XiLexer;
import parser.IXiParser;
import parser.Printer;
import parser.XiParser;
import parser.XiSymbol;

public class Main {

	public static void main(String[] args) {
		boolean lexFlag = false;
		boolean parseFlag = false;
		boolean helpFlag = false;
		String sourcePath = "";
		String dPath = "";

		ArrayList<String> sourceFiles = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--lex")) {
				lexFlag = true;
			} else if (args[i].equals("--parse")) {
				parseFlag = true;
			} else if (args[i].equals("--help")) {
				helpFlag = true;
			} else if (args[i].equals("-sourcepath") && i + 1 < args.length) {
				sourcePath = args[++i];
			} else if (args[i].equals("-D") && i + 1 < args.length) {
				dPath = args[++i];
			} else {
				sourceFiles.add(args[i]);
			}
		}

		if (helpFlag || (!lexFlag && !parseFlag)) {
			displayHelp();
		} else {
			try {
				if (lexFlag) {
					for (String source : sourceFiles) {
						lex(source, sourcePath, dPath);
					}

				}
				if (parseFlag) {
					for (String source : sourceFiles) {
						parse(source, sourcePath, dPath);
					}
				}
			} catch (IOException e) {
				System.out.println("Could not find file");
			}
		}
	}

	private static void lex(String source, String sourceDir, String outputDir) throws IOException {
		String ext = FilenameUtils.getExtension(source);
		if (!ext.equals("xi") && !ext.equals("ixi")) {
			displayHelp();
			return;
		}

        XiLexer lexer = XiLexer.from(sourceDir, source);
        lexer.write(outputDir);
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

		String output;
		if (ext.equals("ixi")) {
			output = ".iparsed";
		} else {
			output = ".parsed";
		}
		output = FilenameUtils.concat(outputDir, FilenameUtils.removeExtension(source) + output);
		source = FilenameUtils.concat(sourceDir, source);

		try {
			if (ext.equals("xi")) {
				OutputStream stream = new FileOutputStream(output);
                XiLexer lexer = XiLexer.from(sourceDir, source);
				XiParser parser = new XiParser(lexer, new ComplexSymbolFactory());

				Printer pp = new Printer(stream);
				Node ast = parser.parse().value();
				Invariant.check(ast);
				pp.print(ast);
			} else if (ext.equals("ixi")) {
				OutputStream stream = new FileOutputStream(output);
				IXiLexer lexer = IXiLexer.from(sourceDir, source);
				IXiParser parser = new IXiParser(lexer, new ComplexSymbolFactory());

				Printer pp = new Printer(stream);
				Node ast = parser.parse().value();
				Invariant.check(ast);
				pp.print(ast);
			} else {
				displayHelp();
			}
		} catch (Exception e) {
			BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
			w.append(e.toString());
			w.close();
			System.out.println(e.toString());

		}
	}

	private static void displayHelp() {
		System.out.println("Usage: xic [options] <source-files>");
		System.out.println("  --help:                 Print a synopsis of options");
		System.out.println(
				"  --lex <source-files>:   For each source file filename.xi, generate a lexed file filename.lexed");
		System.out.println(
				"  --parse <source-files>: For each source file filename.xi/filename.ixi, generate a parsed file filename.parsed/filename.iparsed");
	}

}
