package xic;

import org.apache.commons.io.FilenameUtils;
import ast.Invariant;
import ast.Node;
import lexer.XiLexer;
import parser.IXiParser;
import parser.XiParser;

public class Xic {
	
	private String source;
	private String sink;
	
	public Xic(String source, String sink) {
		this.source = source;
		this.sink = sink;
	}

	public XiLexer lex(String unit) {
		return XiLexer.from(source, unit);
	}
	
	public Node parse(String unit) {
		Node ast = null;
		
		switch (FilenameUtils.getExtension(unit)) {
			case "xi":
				ast = XiParser.from(source, unit);
				break;
			case "ixi":
				ast = IXiParser.from(source, unit);
				break;
			default:
				//TODO: throw XicException
				return null;
		}
		
		Invariant.check(ast);
		return ast;
	}
	
	public void printLexed(String unit) {
		lexer.Printer.print(source, sink, unit);
	}
	
	public void printParsed(String unit) {
		parser.Printer.print(source, sink, unit);
	}
}