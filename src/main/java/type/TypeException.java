package type;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

/**
 * Represents all possible semantic exceptions.
 */
@SuppressWarnings("serial")
public class TypeException extends XicException {

    /**
     * Enums allow for compile-time assurance that all thrown TypeExceptions
     * are drawn from the following set.
     */
    public enum Kind {

        // Object exceptions
        UNBOUND_THIS("Keyword 'this' can only be used inside of a class"),
        UNBOUND_CLASS("Class not found"),
        UNBOUND_FIELD("Field or method not found"),
        UNBOUND_NEW("Keyword 'new' can only be used inside of the class"),
        INVALID_DOT("Left-hand side of dot operator must be a class"),
        NOT_A_METHOD("Expected method"),
        PRIVATE_EQ("Object equality is private to the class definition"),
        MISMATCHED_INTERFACE("Subclass must implement all interface methods"),         

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
        INVALID_BIN_OP("Invalid binary operator for these arguments"),
        LNEG_ERROR("Expected boolean for logical negation"),
        NEG_ERROR("Expected integer for integer negation"),
        INVALID_ARRAY_INDEX("Expected integer for array index"),
        INVALID_ARRAY_SIZE("Expected integer for array size"),
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

    /**
     * Constructor for location-specific error
     *
     * @param kind Kind of TypeException to throw
     * @param location Location of error
     */
    public TypeException(Kind kind, Location location) {
        super(XicException.Kind.SEMANTIC, location, kind.toString());
    }

    /**
     * Constructor for error without location information
     *
     * @param kind Kind of TypeException to throw
     */
    public TypeException(Kind kind) {
        super(XicException.Kind.SEMANTIC, kind.toString());
    }
}
