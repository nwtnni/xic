package xic;

import java_cup.runtime.ComplexSymbolFactory.Location;

/*
 * Ancestor of all compiler exceptions.
 */
@SuppressWarnings("serial")
public class XicException extends Exception {

	public enum Kind {
		LEXICAL("Lexical"),
		SYNTAX("Syntax"),
		SEMANTIC("Semantic");
		
		private String s;
		private Kind(String s) { this.s = s; }
		public String toString() { return s; }
	}

	private Kind kind;
	private Location location;
	private String description;
	
	public XicException(String description) {
		this.kind = null;
		this.location = null;
		this.description = description;
	}
	
	public XicException(Kind kind, String description, Location location) {
		this.kind = kind;
		this.location = location;
		this.description = description;
	}
	
	public String toWrite() {
		if (kind == null || location == null) {
			return description;
		} else {
			return String.format(
				"%d:%d error: %s", 
				location.getLine(),
				location.getColumn(),
				description
			);
		}
		
	}
	
	public String toPrint() {
		if (kind == null || location == null) {
			return description;
		} else {
			return String.format(
				"%s error beginning at %d:%d: %s",
				kind.toString(),
				location.getLine(),
				location.getColumn(),
				description
			);
		}
	}
}