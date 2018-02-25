package parser;

import edu.cornell.cs.cs4120.util.*;
import polyglot.util.OptimalCodeWriter;
import xic.XicException;

import org.apache.commons.io.FilenameUtils;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;

import ast.*;

public class Printer extends Visitor<Void> {

    // TODO: Throw XicException
    public static void print(String source, String sink, String unit) throws XicException {
    	
    	String ext = FilenameUtils.getExtension(unit);
    	Node ast = null;
    	String parsed = sink + FilenameUtils.removeExtension(unit);
    	switch (ext) {
    		case "xi":
    			ast = XiParser.from(source, unit);
    			parsed += ".parsed";
    			break;
    		case "ixi":
    			ast = IXiParser.from(source, unit);
    			parsed += ".iparsed";
    			break;
    		default:
    			throw XicException.unsupported(unit);
    	}
    	
    	String output = FilenameUtils.concat(sink, parsed);
        OutputStream out = null;
        
        try {
            out = new FileOutputStream(output);
            Printer printer = new Printer(out);
            ast.accept(printer);
        } catch (Exception e) {
            try {
                if (out != null) {
                    out.close();
                    BufferedWriter w = new BufferedWriter(new FileWriter(output, false));
                    w.append(e.toString());
                    w.close();
                }
            } catch (Exception io) {}
            System.out.println(e.toString());
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

    public Void visit(Use u){
        printer.startList();

        printer.printAtom("use");
        printer.printAtom(u.file);

        printer.endList();
        return null;
    }

    public Void visit(Fn f){
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

    public Void visit(Var v){
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
