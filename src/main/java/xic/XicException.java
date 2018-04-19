package xic;

import java_cup.runtime.ComplexSymbolFactory.Location;

/**
 * Represents the set of all possible compiler exceptions.
 */
@SuppressWarnings("serial")
public class XicException extends Exception {

    /**
     * Use enums to distinguish the phase of the exception.
     */
    public enum Kind {
        LEXICAL("Lexical"),
        SYNTAX("Syntax"),
        SEMANTIC("Semantic");
        
        private String s;
        private Kind(String s) { this.s = s; }
        public String toString() { return s; }
    }

    /**
     * The phase of compilation this exception occurred in.
     */
    private Kind kind;
    
    /**
     * The source code location the exception occurred at.
     */
    private Location location;
    
    /**
     * The written description of the exception.
     */
    private String description;
    
    /**
     * Creates an exception with no location information. Only used
     * for non-compilation related exceptions (e.g. IOExceptions)
     */
    public XicException(String description) {
        this.kind = null;
        this.location = null;
        this.description = description;
    }

    /**
     * Creates an exception with no location information. Used for
     * a compilation-related exception that can't be associated with
     * a single source code location.
     */
    public XicException(Kind kind, String description) {
        this.kind = kind;
        this.location = null;
        this.description = description;
    }
    
    /**
     * Main constructor with associated compiler phase, source code location,
     * and description.
     */
    public XicException(Kind kind, Location location, String description) {
        this.kind = kind;
        this.location = location;
        this.description = description;
    }
    
    /**
     * Deprecated in favor of more descriptive alternatives, as according
     * to the specification.
     */
    @Deprecated
    public String toString() { return description; }
    
    /**
     * Converts this exception into a file-output suitable format, as according
     * to the specification.
     */
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

    /**
     * Converts this exception into a stdout suitable format, as according
     * to the specification.
     */
    public String toPrint() {
        if (kind == null || location == null) {
            return description;
        } else {
            return String.format(
                "%s error at %s:%d:%d: %s",
                kind.toString(),
                location.getUnit(),
                location.getLine(),
                location.getColumn(),
                description
            );
        }
    }
    
    /**
     * Factory method for a read IO error.
     */
    public static XicException read(String source) {
        return new XicException("Could not read file: " + source);
    }

    /**
     * Factory method for a write IO error.
     */
    public static XicException write(String sink) {
        return new XicException("Could not write file: " + sink);
    }

    /**
     * Factory method for an unsupported file type error.
     */
    public static XicException unsupported(String source) {
        return new XicException("Unsupported file type: " + source);
    }
}