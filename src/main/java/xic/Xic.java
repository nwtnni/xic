package xic;

import ast.Invariant;
import ast.Node;
import lexer.XiLexer;
import parser.IXiParser;
import parser.XiParser;
import type.TypeCheck;

public class Xic {
	
	private String source; // for source path
	private String sink; // for output path
	private String lib; // for use path - interfaces only

	public Xic(String source, String sink, String lib) {
		this.source = source;
		this.sink = sink;
		this.lib = lib;
	}

	public XiLexer lex(String unit) throws XicException {
		switch (FilenameUtils.getExtension(unit)) {
			case "xi":
			case "ixi":
				return XiLexer.from(source, unit);
			default:
				throw XicException.unsupported(unit);
		}
	}
	
	public Node parse(String unit) throws XicException {
		Node ast = null;
		
		switch (FilenameUtils.getExtension(unit)) {
			case "xi":
				ast = XiParser.from(source, unit);
				break;
			case "ixi":
				ast = IXiParser.from(source, unit);
				break;
			default:
				throw XicException.unsupported(unit);
		}
		
		Invariant.check(ast);
		return ast;
	}
	
	public Node typeCheck(String unit) throws XicException {
		Node ast = parse(unit);
		TypeCheck.check(lib, ast);
		return ast;
	}
	
	public void printLexed(String unit) throws XicException {
		lexer.Printer.print(source, sink, unit);
	}
	
	public void printParsed(String unit) throws XicException {
		parser.Printer.print(source, sink, unit);
	}

	public void printTyped(String unit) throws XicException {
		type.Printer.print(source, sink, lib, unit);
	}
}