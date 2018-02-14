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
        printer.startUnifiedList();
        
        // Use statements
        printer.startUnifiedList();
        for (Node use : p.uses) {
            use.accept(this);
        }
        printer.endList();

        // Function declarations
        printer.startUnifiedList();
        for (Node function : p.functions) {
            function.accept(this);
        }
        printer.endList();

        printer.endList();
    }

    public void visit(Use u){
        printer.startList();

        printer.printAtom("use");
        printer.printAtom(u.file);

        printer.endList();
    }

    public void visit(Function f){
        printer.startList();

        // Function name
        printer.printAtom(f.id);
        
        // Function arguments
        printer.startList();
        for (Node arg : f.args) {
            arg.accept(this);
        }
        printer.endList();

        // Function return types
        printer.startList();
        for (Node type : f.types) {
            type.accept(this);
        }
        printer.endList();

        // Statement block
        f.block.accept(this);

        printer.endList();
    }

    /*
     * Statement nodes
     */
    public void visit(Declare d){
        printer.startList(); 

        // Variable name
        d.id.accept(this);

        // Type
        d.type.accept(this);

        printer.endList();
    }

    public void visit(Assign a){
        printer.startList();

        printer.printAtom("=");

        a.lhs.accept(this);
        a.rhs.accept(this);

        printer.endList();
    }

    public void visit(Return r){
        printer.startList();

        printer.printAtom("return");
        
        if (r.value != null) {
            r.value.accept(this);
        }

        printer.endList();
    }

    public void visit(Block b){
        printer.startUnifiedList();

        for (Node statement : b.statements) {
            statement.accept(this);
        }

        printer.endList();
    }

    public void visit(If i){
        printer.startUnifiedList();

        printer.printAtom("if");
        
        i.guard.accept(this);
        i.block.accept(this);

        printer.endList();
    }

    public void visit(Else e){
        printer.startUnifiedList();

        printer.printAtom("else");
        e.block.accept(this);

        printer.endList();
    }

    public void visit(While w){
        printer.startUnifiedList();

        printer.printAtom("while");
        w.guard.accept(this);
        w.block.accept(this);

        printer.endList();
    }

    /*
     * Expression nodes
     */
    public void visit(Binary b){
        printer.startList();

        printer.printAtom(b.btype.toString());
        b.lhs.accept(this);
        b.rhs.accept(this);

        printer.endList();
    }

    public void visit(Unary u){
        printer.startList();

        printer.printAtom(u.utype.toString());
        u.child.accept(this);

        printer.endList();
    }

    public void visit(Variable v){
    }

    public void visit(Multiple m){
    }

    public void visit(Index i){
    }

    public void visit(Call c){
    }

    public void visit(Type t) {
    
    }

    public void visit(XiInt i){
        
    }

    public void visit(XiBool b){
    }

    public void visit(XiChar c){
    }

    public void visit(XiString s){
    }

    public void visit(XiArray a){
    }
}
