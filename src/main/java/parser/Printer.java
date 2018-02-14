package parser;

import edu.cornell.cs.cs4120.util.*;
import polyglot.util.OptimalCodeWriter;

import java.io.OutputStream;

public class Printer implements Visitor {

    private static final int WIDTH = 80;
    private SExpPrinter printer;

    public Printer(OutputStream stream) {
        printer = new CodeWriterSExpPrinter(new OptimalCodeWriter(stream, WIDTH));
    }

    public void print(Node n) {
        n.accept(this);
    }

    /*
     * Top-level AST nodes
     */
    public void visit(Program p){
           
    }

    public void visit(Use u){
    }

    public void visit(Function f){
    }

    /*
     * Statement nodes
     */
    public void visit(Declare d){
    }

    public void visit(Assign a){
    }

    public void visit(Return r){
    }

    public void visit(Block b){
    }

    public void visit(If i){
    }

    public void visit(Else e){
    }

    public void visit(While w){
    }

    /*
     * Expression nodes
     */
    public void visit(Binary b){
    }

    public void visit(Unary u){
    }

    public void visit(Value v){
    }

    public void visit(Variable v){
    }

    public void visit(Multiple m){
    }

    public void visit(Index i){
    }

    public void visit(Call c){
    }
}
