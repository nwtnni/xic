package ast;

import java.util.List;
import java.util.ArrayList;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;
import xic.XicInternalException;

// Function Declaration
public class XiFn extends TopDeclr {
    
    public enum Kind {
        FN, FN_HEADER,
        PROC, PROC_HEADER
    }

    public Kind kind;
    public String id;
    public List<Node> args;
    public List<Node> returns;
    public Node block;

    public XiFn(Location location, String id, List<Node> args, List<Node> returns) {
        this.kind = Kind.FN_HEADER;
        this.location = location;
        this.id = id;  
        this.args = args;
        this.returns = returns;
        this.block = null;
    }

    public XiFn(Location location, String id, List<Node> args) {
        this.kind = Kind.PROC_HEADER; 
        this.location = location;
        this.id = id; 
        this.args = args;
        this.returns = new ArrayList<>();
        this.block = null;
    }

    public XiFn(Location location, XiFn f, Node block) {
        switch (f.kind) {
            case FN_HEADER:
                this.kind = Kind.FN;
                break;
            case PROC_HEADER:
                this.kind = Kind.PROC;
                break;
            default:
                throw XicInternalException.runtime("Not a function declaration.");
        }

        this.location = location;
        this.id = f.id;
        this.args = f.args;
        this.returns = f.returns;
        this.block = block;
    }

    public boolean isFn() {
        return kind == Kind.FN || kind == Kind.FN_HEADER; 
    }

    public boolean isDef() {
        return kind == Kind.FN || kind == Kind.PROC;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
