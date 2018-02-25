package type;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;
import ast.*;

@SuppressWarnings("serial")
public class TypeException extends XicException {

    public enum Kind {
    	
    	DECLARATION_CONFLICT("conflicting declarations"),
        
        SYMBOL_NOT_FOUND("Symbol not found: ");

        private String message;

        private Kind(String message) {
            this.message = message; 
        }

        public String toString() {
            return message; 
        }
    }
    
    public TypeException(Kind kind, Location location) {
    	super(XicException.Kind.SEMANTIC, location, kind.toString());
    }

    public TypeException(Kind kind, Node node) {
    	super(XicException.Kind.SEMANTIC, node.location, kind.toString());
    }
}
