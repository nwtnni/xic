package parse;

import java.util.List;
import java.io.*;

import edu.cornell.cs.cs4120.util.*;
import polyglot.util.OptimalCodeWriter;
import util.Filename;
import ast.*;
import xic.XicException;

/**
 * Recursively traverses a parsed AST and writes a pretty-printed
 * version to file.
 */
public class Printer extends Visitor<Void> {

    /**
     * Parses the given file, and outputs diagnostic
     * information to the given output file.
     * 
     * @param source Directory to search for the source
     * @param sink Directory to output the result
      * @param unit Path to the target source file, relative to source
     * @throws XicException if the Printer was unable to write to the given file
     */
    public static void print(String source, String sink, String unit) throws XicException {
        String ext = Filename.getExtension(unit);
        String output = Filename.concat(sink, unit);

        try {
            Filename.makePathTo(output);
            try {
                Node ast = null;
                switch (ext) {
                    case "xi":
                        output = Filename.setExtension(output, "parsed");
                        ast = XiParser.from(source, unit);
                        break;
                    case "ixi":
                        output = Filename.setExtension(output, "iparsed");
                        ast = IXiParser.from(source, unit);
                        break;
                    default:
                        throw XicException.unsupported(unit);
                }
        
                OutputStream stream = new FileOutputStream(output);
                Printer printer = new Printer(stream);
                ast.accept(printer);
            } catch (XicException xic) {
                BufferedWriter w = new BufferedWriter(new FileWriter(output));
                w.write(xic.toWrite());
                w.close();
                throw xic;
            }
        } catch (IOException io) {
            throw XicException.write(output);
        }
    }

    private static final int WIDTH = 80;
    private SExpPrinter printer;

    private Printer(OutputStream stream) {
        printer = new CodeWriterSExpPrinter(new OptimalCodeWriter(stream, WIDTH));
    }
    
    /* 
     * Visitor logic
     */

    /*
     * Top-level AST nodes
     */
    public Void visit(Program p) throws XicException{
        printer.startUnifiedList();
        
        // Use statements
        if (p.isProgram()) {
            printer.startUnifiedList();
            visit(p.uses);
            printer.endList();
        }

        // Fn declarations
        printer.startUnifiedList();
        visit(p.fns);
        printer.endList();

        printer.endList();
        printer.flush();
        return null;
    }

    public Void visit(Use u) throws XicException{
        printer.startList();

        printer.printAtom("use");
        printer.printAtom(u.file);

        printer.endList();
        return null;
    }

    public Void visit(Fn f) throws XicException{
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
    public Void visit(Declare d) throws XicException{
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

    public Void visit(Assign a) throws XicException{
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

    public Void visit(Return r) throws XicException{
        printer.startList();

        printer.printAtom("return");
        
        if (r.hasValues()) {
            visit(r.values);
        }

        printer.endList();
        return null;
    }

    public Void visit(Block b) throws XicException{
        printer.startUnifiedList();
        visit(b.statements);
        printer.endList();
        return null;
    }

    public Void visit(If i) throws XicException{
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

    public Void visit(While w) throws XicException{
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
    public Void visit(Call c) throws XicException{
        printer.startList();

        printer.printAtom(c.id);
        visit(c.args);
        printer.endList();
        return null;
    }

    public Void visit(Binary b) throws XicException{
        printer.startList();

        printer.printAtom(b.kind.toString());
        b.lhs.accept(this);
        b.rhs.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(Unary u) throws XicException{
        printer.startList();

        printer.printAtom(u.kind.toString());
        u.child.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(Var v) throws XicException{
        printer.printAtom(v.id);
        return null;
    }

    public Void visit(Index i) throws XicException{
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