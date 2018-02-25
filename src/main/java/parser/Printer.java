package parser;

import edu.cornell.cs.cs4120.util.*;
import polyglot.util.OptimalCodeWriter;
import xic.XicException;

import org.apache.commons.io.FilenameUtils;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import ast.*;

public class Printer extends Visitor<Void> {

    public static void print(String source, String sink, String unit) throws XicException {

    	String ext = FilenameUtils.getExtension(unit);
    	String output = FilenameUtils.concat(sink, FilenameUtils.removeExtension(unit));
    	Node ast = null;
        OutputStream stream = null;

        try {
        	try {
            	switch (ext) {
	        		case "xi":
	        			output += ".parsed";
	        			ast = XiParser.from(source, unit);
	        			break;
	        		case "ixi":
	        			output += ".iparsed";
	        			ast = IXiParser.from(source, unit);
	        			break;
	        		default:
	        			throw XicException.unsupported(unit);
            	}
    	
	            stream = new FileOutputStream(output);
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
     * Top-level AST nodes
     */
    public Void visit(Program p) throws XicException{
        printer.startUnifiedList();
        
        // Use statements
        if (p.isProgram()) {
            printer.startUnifiedList();
            for (Node use : p.uses) {
                use.accept(this);
            }
            printer.endList();
        }

        // Fn declarations
        printer.startUnifiedList();
        for (Node f : p.fns) {
            f.accept(this);
        }
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
        for (Node arg : f.args) {
            arg.accept(this);
        }
        printer.endList();

        // Fn return types
        printer.startList();
        if (f.isFn()) {
            for (Node type : f.returns) {
                type.accept(this);
            }
        }
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
            d.id.accept(this);
            d.type.accept(this);
            printer.endList();
        }
        return null;
    }

    public Void visit(Assign a) throws XicException{
        printer.startList();

        printer.printAtom("=");
        a.lhs.accept(this);
        a.rhs.accept(this);

        printer.endList();
        return null;
    }

    public Void visit(Return r) throws XicException{
        printer.startList();

        printer.printAtom("return");
        
        if (r.hasValue()) {
            r.value.accept(this);
        }

        printer.endList();
        return null;
    }

    public Void visit(Block b) throws XicException{
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

    public Void visit(Else e) throws XicException{
        e.block.accept(this);
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

        c.id.accept(this);
        
        for (Node arg : c.args) {
            arg.accept(this);
        }

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

    public Void visit(Multiple m) throws XicException{
        boolean isParen = m.isAssign() && m.values.size() > 1;

        if (isParen) { printer.startList(); }

        for (Node value : m.values) {
            value.accept(this);
        }

        if (isParen) { printer.endList(); }
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
        printer.printAtom(Long.toString(i.value));
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

        for (Node value: a.values) {
            value.accept(this);
        }

        printer.endList();
        return null;
    }
}
