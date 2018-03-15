package ir;

import java.io.*;
import xic.FilenameUtils;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;

import xic.XicException;
import emit.Emitter;
import interpret.IRSimulator;
import parser.XiParser;
import type.TypeChecker;
import type.FnContext;
import ast.Node;
import ast.Program;

public class Printer extends IRVisitor<Void> {

	/**
	 * Parses the given file, runs the type checker, generates IR 
     * and outputs diagnostic information to the given output file.
	 * 
	 * @param source Directory to search for the source
	 * @param sink Directory to output the result
	 * @param lib Directory to search for interface files
 	 * @param unit Path to the target source file, relative to source
	 * @param run Run IR interpreter on generated IR code
	 * @param opt enable optimizations if true 
	 * @throws XicException if the Printer was unable to write to the given file
	 */
    public static void print(String source, String sink, String lib, String unit, boolean run, boolean opt) throws XicException {
        String output = FilenameUtils.concat(sink, FilenameUtils.removeExtension(unit));
	    output = FilenameUtils.setExtension(output, "ir");

        IRCompUnit comp = null;

        try {
            FilenameUtils.makePathTo(output);
        	try {
                Node ast = XiParser.from(source, unit);
                FnContext context = TypeChecker.check(lib, ast);
                comp = Emitter.emitIR((Program) ast, context);

                // Generate .ir file
	            OutputStream stream = new FileOutputStream(output);
                Printer p = new Printer(stream);
                comp.accept(p);

                // Print IR to console
                debug(comp);

                // System.out.println("\nRunning example: \n");

                // IRStmt cBody =
        		// 	new IRSeq(new IRExp(new IRCall(new IRName("_Iprintln_pai"),
        		// 						new IRCall(new IRName("_IunparseInt_aii"),
        		// 									new IRConst(5)
        		// 								))),
        		// 			new IRReturn());
                // IRFuncDecl func = new IRFuncDecl("_Imain_paai", cBody);

                // IRCompUnit compUnit = new IRCompUnit("test");
                // compUnit.appendFunc(func);

                // System.out.println("Generated code: \n");
                // debug(compUnit);
                // compUnit.accept(p);
                // System.out.println("\nEvaluating: \n");
                // (new IRSimulator(compUnit)).call("_Imain_paai", 0);

                if (run) {
                    IRSimulator sim = new IRSimulator(comp);
                    sim.call("_Imain_paai", 0);
                }

	    	} catch (XicException xic) {
	            throw xic;
	    	}
        } catch (IOException io) {
        	throw XicException.write(output);
        }
    }

    public static void debug(IRNode ast) {
        Printer p = new Printer(new PrintWriter(System.out));
        ast.accept(p);
    }

    public static String toString(IRNode ast) {
        StringWriter sw = new StringWriter();
        Printer p = new Printer(new PrintWriter(sw));
        ast.accept(p);
        return sw.toString();
    }

    private final int WIDTH = 80;
    public SExpPrinter printer;

    public Printer(OutputStream stream) {
        printer = new CodeWriterSExpPrinter(new OptimalCodeWriter(stream, WIDTH));
    }

    public Printer(PrintWriter writer) {
        printer = new CodeWriterSExpPrinter(writer);
    }

    /*
     * Visitor methods
     */

	public Void visit(IRBinOp b) {
        printer.startList();
        printer.printAtom(b.type.toString());
        b.left.accept(this);
        b.right.accept(this);
        printer.endList();
        return null;
	}
	
	public Void visit(IRCall c) {
        printer.startList();
        printer.printAtom("CALL");
        c.target.accept(this);
        for (IRNode arg : c.args) {
            arg.accept(this);
        }
        printer.endList();
		return null;
	}

	public Void visit(IRCJump c) {
        printer.startList();
        printer.printAtom("CJUMP");
        c.cond.accept(this);
        printer.printAtom(c.trueLabel);
        if (c.hasFalseLabel()) {
            printer.printAtom(c.falseLabel);
        }
        printer.endList();
		return null;
	}

	public Void visit(IRJump j) {
        printer.startList();
        printer.printAtom("JUMP");
        j.target.accept(this);
        printer.endList();
		return null;
	}
	
	public Void visit(IRCompUnit c) {
        printer.startUnifiedList();
        printer.printAtom("COMPUNIT");
        printer.printAtom(c.name);
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        printer.endList();
        printer.flush();
		return null;
	}

	public Void visit(IRConst c) {
        printer.startList();
        printer.printAtom("CONST");
        printer.printAtom(String.valueOf(c.value));
        printer.endList();
		return null;
	}

	public Void visit(IRESeq e) {
        printer.startList();
        printer.printAtom("ESEQ");
        e.stmt.accept(this);
        e.expr.accept(this);
        printer.endList();
		return null;
	}

	public Void visit(IRExp e) {
        printer.startList();
        printer.printAtom("EXP");
        e.expr.accept(this);
        printer.endList();
		return null;
	}

	public Void visit(IRFuncDecl f) {
        printer.startList();
        printer.printAtom("FUNC");
        printer.printAtom(f.name);
        f.body.accept(this);
        printer.endList();
		return null;
	}

	public Void visit(IRLabel l) {
        printer.startList();
        printer.printAtom("LABEL");
        printer.printAtom(l.name);
        printer.endList();
		return null;
	}

	public Void visit(IRMem m) {
        printer.startList();
        printer.printAtom(m.memType.toString());
        m.expr.accept(this);
        printer.endList();
		return null;
	}

	public Void visit(IRMove m) {
        printer.startList();
        printer.printAtom("MOVE");
        m.target.accept(this);
        m.src.accept(this);
        printer.endList();
		return null;
	}

	public Void visit(IRName n) {
        printer.startList();
        printer.printAtom("NAME");
        printer.printAtom(n.name);
        printer.endList();
		return null;
	}

	public Void visit(IRReturn r) {
        printer.startList();
        printer.printAtom("RETURN");
        for (IRNode ret : r.rets) {
            ret.accept(this);
        }
        printer.endList();
		return null;
	}

	public Void visit(IRSeq s) {
        printer.startUnifiedList();
        printer.printAtom("SEQ");
        for (IRNode stmt : s.stmts) {
            stmt.accept(this);
        }
        printer.endList();
		return null;
	}

	public Void visit(IRTemp t) {
        printer.startList();
        printer.printAtom("TEMP");
        printer.printAtom(t.name);
        printer.endList();
		return null;
	}
}