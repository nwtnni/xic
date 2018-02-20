package type;

import java_cup.runtime.ComplexSymbolFactory.Location;
import ast.*;

public class TypeException extends Exception {

    public enum Kind {
        
        SYMBOL_NOT_FOUND("Symbol not found: ");

        private String message;

        private Kind(String message) {
            this.message = message; 
        }

        public String toString() {
            return message; 
        }
    }

    private Kind kind;
    private Location location;
    
    public TypeException(Kind kind, Node node) {
        this.location = location; 
    }
}
