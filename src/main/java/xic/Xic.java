package xic;

import ast.Invariant;
import ast.Node;
import lexer.XiLexer;
import parser.IXiParser;
import parser.Printer;
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
	
	public Node parseSource(String unit) {
		Node ast = XiParser.invoke(source, unit);
		Invariant.check(ast);
		return ast;
	}
	
	public Node parseInterface(String unit) {
		Node ast = IXiParser.invoke(source, unit); 
		Invariant.check(ast);
		return ast;
	}
	
	public void writeLex(String unit) {
		XiLexer lexer = lex(unit);
		lexer.write(unit);
	}
	
	public void writeSource(String unit) {
		Printer.writeSource(source, sink, unit);
	}
	
	public void writeInterface(String unit) {
		Printer.writeInterface(source, sink, unit);
	}
}