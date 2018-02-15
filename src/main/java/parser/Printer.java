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
        printer.flush();
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

    public void visit(ProcedureCall p) {
        p.id.accept(this);
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
    public void visit(FunctionCall c){
        printer.startList();

        c.id.accept(this);
        
        for (Node arg : c.args) {
            arg.accept(this);
        }

        printer.endList();
    }

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
        printer.printAtom(v.id);
    }

    public void visit(Multiple m){
        printer.startList();

        for (Node value : m.values) {
            value.accept(this);
        }

        printer.endList();
    }

    public void visit(Index i){
        printer.startList();

        printer.printAtom("[]");
        i.array.accept(this);
        i.index.accept(this);

        printer.endList();
    }

    public void visit(Type t) {
        if (t.primitive.equals(Type.Primitive.ARRAY)) {
            printer.startList();
            t.child.accept(this);
            printer.endList();

            if (t.size != null) {
                t.size.accept(this);
            }
        } else {
            printer.printAtom(t.primitive.toString());
        }
    }

    public void visit(XiInt i) {
        printer.printAtom(Long.toString(i.value));
    }

    public void visit(XiBool b) {
        printer.printAtom(Boolean.toString(b.value));
    }

    public void visit(XiChar c) {
        printer.printAtom(c.escaped);
    }

    public void visit(XiString s) {
        printer.printAtom(s.escaped);
    }

    public void visit(XiArray a) {
        printer.startList();

        for (Node value: a.values) {
            value.accept(this);
        }

        printer.endList();
    }
}
