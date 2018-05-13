package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A use statement
public class XiUse extends TopDeclr {
    
    public String file;

    public XiUse(Location location, String file) {
        this.location = location;
        this.file = file; 
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
