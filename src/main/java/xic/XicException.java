package xic;

import java_cup.runtime.ComplexSymbolFactory.Location;

/*
 * Ancestor of all compiler exceptions.
 */
@SuppressWarnings("serial")
public class XicException extends Exception {
	
	private static final String prefix = "Xic raised exception: ";	
	
	private String message;
	private Location location;
	
	// Require all XicExceptions to have at least a message
	@SuppressWarnings("unused")
	private XicException() {}
	
	public XicException(String message) {
		this.message = message;
		this.location = null;
	}
	
	public XicException(String message, Location location) {
		this.message = message;
		this.location = location;
	}
	
	@Override
	public String toString() {
		String error = prefix + message;
		
		if (location != null) {
			error += String.format(
				" in file %s, at line %d : column %d",
				location.getUnit(),
				location.getLine(),
				location.getColumn()
			);
		}
		
		return error;
	}
	
	
}