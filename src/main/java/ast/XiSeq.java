package ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java_cup.runtime.ComplexSymbolFactory.Location;
import xic.XicException;


// A Block of statements
public class XiSeq extends Stmt {

    public List<Node> stmts;

    public XiSeq(Location location, List<Node> statements) {
        this.location = location;
        this.stmts = statements;
    }

    public XiSeq(Location location, Node... statements) {
        this.location = location;
        this.stmts = new ArrayList<>(Arrays.asList(statements));
    }
    
    public XiSeq(List<Node> statements) {
        this.stmts = statements;
    }

    public <T> T accept(ASTVisitor<T> v) throws XicException {
        return v.visit(this);
    }
}
