package type;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;
import ast.*;

@SuppressWarnings("serial")
public class TypeException extends XicException {

    public enum Kind {
        // Statement exceptions
    	CONTROL_FLOW("Control reached end of non-void function"),
        MISMATCHED_ASSIGN("Mismatched types for assignment"),
        INVALID_WILDCARD("Wildcard can only be used with function call"),
    	MISMATCHED_RETURN("Mismatched return type for function"),
        UNUSED_FUNCTION("Unused function result"),
    	UNREACHABLE("Unreachable statement"),
    	INVALID_GUARD("Guard expressions must be type bool"),
        
        // Expression exceptions
        INVALID_ARG_TYPES("Invalid arguments for function call"),
        MISMATCHED_BINARY("Mismatched types for binary operator"),
        INVALID_INT_OP("Invalid binary operator for integers"),
        INVALID_BIN_OP("Invalid binary operator for these arguments"),
        LNEG_ERROR("Expected boolean for logical negation"),
        NEG_ERROR("Expected integer for integer negation"),
        INVALID_ARRAY_INDEX("Expected integer for array index"),
        NOT_UNIFORM_ARRAY("Arrays must contain values of uniform type"),
        NOT_AN_ARRAY("Expected array"),
        
        // Context exceptions
    	DECLARATION_CONFLICT("Conflicting declarations"),
        SYMBOL_NOT_FOUND("Symbol not found"),
        CYCLIC_TYPE_DEPENDENCY("Found cyclic type dependency");

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

    public TypeException(Kind kind) {
        super(XicException.Kind.SEMANTIC, kind.toString());
    }
}
