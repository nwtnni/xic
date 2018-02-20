package parser;

import edu.cornell.cs.cs4120.util.*;
import polyglot.util.OptimalCodeWriter;

import java.io.OutputStream;

import ast.*;

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
        if (p.isProgram()) {
            printer.startUnifiedList();
            for (Node use : p.uses) {
                use.accept(this);
            }
            printer.endList();
        }

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
        if (f.isFunction()) {
            for (Node type : f.types) {
                type.accept(this);
            }
        }
        printer.endList();

        // Statement block
        if (f.isDefinition()) {
            f.block.accept(this);
        }

        printer.endList();
    }

    /*
     * Statement nodes
     */
    public void visit(Declare d){
        

        //TODO
        if(d.id != null && d.type != null) {
            printer.startList(); 
            d.id.accept(this);
            d.type.accept(this);
            printer.endList();
        }
        else {
            printer.printAtom("_");
        }

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
        
        if (r.hasValue()) {
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
        //TODO
        if (i.elseBlock != null) {
            i.elseBlock.accept(this);
        }

        printer.endList();
    }

    public void visit(Else e){
        e.block.accept(this);
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
    public void visit(Call c){
        printer.startList();

        c.id.accept(this);
        
        for (Node arg : c.args) {
            arg.accept(this);
        }

        printer.endList();
    }

    public void visit(Binary b){
        printer.startList();

        printer.printAtom(b.kind.toString());
        b.lhs.accept(this);
        b.rhs.accept(this);

        printer.endList();
    }

    public void visit(Unary u){
        printer.startList();

        printer.printAtom(u.kind.toString());
        u.child.accept(this);

        printer.endList();
    }

    public void visit(Variable v){
        printer.printAtom(v.id);
    }

    public void visit(Multiple m){
        boolean isParen = m.isAssign() && m.values.size() > 1;

        if (isParen) { printer.startList(); }

        for (Node value : m.values) {
            value.accept(this);
        }

        if (isParen) { printer.endList(); }
    }

    public void visit(Index i){
        printer.startList();

        printer.printAtom("[]");
        i.array.accept(this);
        i.index.accept(this);

        printer.endList();
    }

    public void visit(XiType t) {
        if (t.kind.equals(XiType.Kind.ARRAY)) {
            printer.startList();
            printer.printAtom("[]");
            t.child.accept(this);

            if (t.size != null) {
                t.size.accept(this);
            }
            printer.endList();
        } else {
            printer.printAtom(t.kind.toString());
        }
    }

    public void visit(XiInt i) {
        printer.printAtom(Long.toString(i.value));
    }

    public void visit(XiBool b) {
        printer.printAtom(Boolean.toString(b.value));
    }

    public void visit(XiChar c) {
        printer.printAtom("\'"+c.escaped+"\'");
    }

    public void visit(XiString s) {
        printer.printAtom("\""+s.escaped+"\"");
    }

    public void visit(XiArray a) {
        printer.startList();

        for (Node value: a.values) {
            value.accept(this);
        }

        printer.endList();
    }
}
