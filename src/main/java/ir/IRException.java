package ir;

import xic.XicException;
import java_cup.runtime.ComplexSymbolFactory.Location;;

//TODO Fill this class out

@SuppressWarnings("serial")
public class IRException extends XicException {

    public enum Kind {

    }

    /**
     * Constructor for location-specific error
     * 
     * @param kind Kind of TypeException to throw
     * @param location Location of error
     */
    public IRException(Kind kind, Location location) {
    	super(XicException.Kind.SEMANTIC, location, kind.toString());
    }

}