package ast;

import java.util.List;
import java.util.ArrayList;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;

// A Function Call
public class XiCall extends Expr {
    
    public Node id;
    public List<Node> args; 
    
    public XiCall(Location location, Node id, List<Node> args) {
        this.location = location;
        this.id = id; 
        this.args = args;
    }

    public XiCall(Location location, Node id, Node arg) {
        this.location = location;
        this.id = id;
        this.args = new ArrayList<>();
        args.add(arg);
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }

    public List<Node> getArgs() {
        return args;
    }
}
