package parser;

import edu.cornell.cs.cs4120.util.*;
import polyglot.util.OptimalCodeWriter;

import java.io.OutputStream;

import ast.*;

public class Printer extends Visitor<Void> {

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
    public Void visit(Program p){
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
        return null;
    }

    public Void visit(Use u){
        printer.startList();

        printer.printAtom("use");
        printer.printAtom(u.file);

        printer.endList();
        return null;
    }

    public Void visit(Function f){
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
        return null;
    }

    /*
     * Statement nodes
     */
    public Void visit(Declare d){
        if (d.isUnderscore()) {
            printer.printAtom("_");
        }
        else {
            printer.startList(); 
            d.id.accept(this);
            d.type.accept(this);
            printer.endList();
        }
        return null;
    }

    public Void visit(Assign a){
        printer.startList();

        printer.printAtom("=");
        a.lhs.accept(this);
        a.rhs.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(Return r){
        printer.startList();

        printer.printAtom("return");
        
        if (r.hasValue()) {
            r.value.accept(this);
        }

        printer.endList();
        return null;
    }

    public Void visit(Block b){
        printer.startUnifiedList();

        for (Node statement : b.statements) {
            statement.accept(this);
        }
        
        if (b.hasReturn()) {
            b.returns.accept(this);
        }

        printer.endList();
        return null;
    }

    public Void visit(If i){
        printer.startUnifiedList();

        printer.printAtom("if");
        
        i.guard.accept(this);
        i.block.accept(this);

        if (i.hasElse()) {
            i.elseBlock.accept(this);
        }

        printer.endList();
        return null;
    }

    public Void visit(Else e){
        e.block.accept(this);
        return null;
    }

    public Void visit(While w){
        printer.startUnifiedList();

        printer.printAtom("while");
        w.guard.accept(this);
        w.block.accept(this);

        printer.endList();
        return null;
    }

    /*
     * Expression nodes
     */
    public Void visit(Call c){
        printer.startList();

        c.id.accept(this);
        
        for (Node arg : c.args) {
            arg.accept(this);
        }

        printer.endList();
        return null;
    }

    public Void visit(Binary b){
        printer.startList();

        printer.printAtom(b.kind.toString());
        b.lhs.accept(this);
        b.rhs.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(Unary u){
        printer.startList();

        printer.printAtom(u.kind.toString());
        u.child.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(Variable v){
        printer.printAtom(v.id);
        return null;
    }

    public Void visit(Multiple m){
        boolean isParen = m.isAssign() && m.values.size() > 1;

        if (isParen) { printer.startList(); }

        for (Node value : m.values) {
            value.accept(this);
        }

        if (isParen) { printer.endList(); }
        return null;
    }

    public Void visit(Index i){
        printer.startList();

        printer.printAtom("[]");
        i.array.accept(this);
        i.index.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(XiType t) {
        if (t.kind.equals(XiType.Kind.ARRAY)) {
            printer.startList();
            printer.printAtom("[]");
            t.child.accept(this);

            if (t.size != null) {
                t.size.accept(this);
            }
            printer.endList();
        } else {
            printer.printAtom(t.id);
        }
        return null;
    }

    public Void visit(XiInt i) {
        printer.printAtom(Long.toString(i.value));
        return null;
    }

    public Void visit(XiBool b) {
        printer.printAtom(Boolean.toString(b.value));
        return null;
    }

    public Void visit(XiChar c) {
        printer.printAtom("\'"+c.escaped+"\'");
        return null;
    }

    public Void visit(XiString s) {
        printer.printAtom("\""+s.escaped+"\"");
        return null;
    }

    public Void visit(XiArray a) {
        printer.startList();

        for (Node value: a.values) {
            value.accept(this);
        }

        printer.endList();
        return null;
    }
}
