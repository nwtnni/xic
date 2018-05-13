package parse;

import java.io.*;

import edu.cornell.cs.cs4120.util.*;
import polyglot.util.OptimalCodeWriter;
import ast.*;
import xic.XicException;

/**
 * Recursively traverses a parsed AST and writes a pretty-printed
 * version to file.
 */
public class Printer extends ASTVisitor<Void> {

    private static final int WIDTH = 200;
    private SExpPrinter printer;

    public Printer(OutputStream stream) {
        printer = new CodeWriterSExpPrinter(new OptimalCodeWriter(stream, WIDTH));
    }
    
    /* 
     * Visitor logic
     */

    /*
     * Top-level AST nodes
     */
    public Void visit(XiProgram p) throws XicException{
        printer.startUnifiedList();
        
        // Use statements
        if (p.isProgram()) {
            printer.startUnifiedList();
            visit(p.uses);
            printer.endList();
        }

        // Fn declarations
        printer.startUnifiedList();
        visit(p.body);
        printer.endList();

        printer.endList();
        printer.flush();
        return null;
    }

    public Void visit(XiUse u) throws XicException{
        printer.startList();

        printer.printAtom("use");
        printer.printAtom(u.file);

        printer.endList();
        return null;
    }

    public Void visit(XiFn f) throws XicException{
        printer.startList();

        // Fn name
        printer.printAtom(f.id);
        
        // Fn arguments
        printer.startList();
        visit(f.args);
        printer.endList();

        // Fn return types
        printer.startList();
        visit(f.returns);
        printer.endList();

        // Statement block
        if (f.isDef()) {
            f.block.accept(this);
        }

        printer.endList();
        return null;
    }

    /*
     * Statement nodes
     */
    public Void visit(XiDeclr d) throws XicException{
        if (d.isUnderscore()) {
            printer.printAtom("_");
        }
        else {
            printer.startList(); 
            printer.printAtom(d.id);
            d.xiType.accept(this);
            printer.endList();
        }
        return null;
    }

    public Void visit(XiAssign a) throws XicException{
        printer.startList();

        printer.printAtom("=");
        if (a.lhs.size() > 1) {
            printer.startList();
            visit(a.lhs);
            printer.endList();
        } else {
            visit(a.lhs);
        }
        a.rhs.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(XiReturn r) throws XicException{
        printer.startList();

        printer.printAtom("return");
        
        if (r.hasValues()) {
            visit(r.values);
        }

        printer.endList();
        return null;
    }

    public Void visit(XiBlock b) throws XicException{
        printer.startUnifiedList();
        visit(b.statements);
        printer.endList();
        return null;
    }

    public Void visit(XiIf i) throws XicException{
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

    public Void visit(XiWhile w) throws XicException{
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
    public Void visit(XiCall c) throws XicException{
        printer.startList();

        printer.printAtom(c.id);
        visit(c.args);
        printer.endList();
        return null;
    }

    public Void visit(XiBinary b) throws XicException{
        printer.startList();

        printer.printAtom(b.kind.toString());
        b.lhs.accept(this);
        b.rhs.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(XiUnary u) throws XicException{
        printer.startList();

        printer.printAtom(u.kind.toString());
        u.child.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(XiVar v) throws XicException{
        printer.printAtom(v.id);
        return null;
    }

    public Void visit(XiIndex i) throws XicException{
        printer.startList();

        printer.printAtom("[]");
        i.array.accept(this);
        i.index.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(XiType t) throws XicException {
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

    public Void visit(XiInt i) throws XicException {
        if (i.negated) {
            printer.startList();
            printer.printAtom("-");
            printer.printAtom(i.literal);
            printer.endList();
        } else {
            printer.printAtom(i.literal);
        }
        return null;
    }

    public Void visit(XiBool b) throws XicException {
        printer.printAtom(Boolean.toString(b.value));
        return null;
    }

    public Void visit(XiChar c) throws XicException {
        printer.printAtom("\'"+c.escaped+"\'");
        return null;
    }

    public Void visit(XiString s) throws XicException {
        printer.printAtom("\""+s.escaped+"\"");
        return null;
    }

    public Void visit(XiArray a) throws XicException {
        printer.startList();
        visit(a.values);
        printer.endList();
        return null;
    }
}
