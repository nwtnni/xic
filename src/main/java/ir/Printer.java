package ir;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;

public class Printer extends IRVisitor<Void> {


    public SExpPrinter p;

    public Printer(PrintWriter stream) {
        this.p = new CodeWriterSExpPrinter(stream);
    }

    public static void debug(IRNode ast) {
        Printer p = new Printer(new PrintWriter(System.out));
        ast.accept(p);
    }

    public static String toString(IRNode ast) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Printer p = new Printer(new PrintWriter(stream));
        ast.accept(p);
        return stream.toString();
    }

	public Void visit(IRBinOp b) {
        p.startList();
        p.printAtom(b.type.toString());
        b.left.accept(this);
        b.right.accept(this);
        p.endList();
        return null;
	}
	
	public Void visit(IRCall c) {
        p.startList();
        p.printAtom("CALL");
        c.target.accept(this);
        for (IRNode arg : c.args) {
            arg.accept(this);
        }
        p.endList();
		return null;
	}

	public Void visit(IRCJump c) {
        p.startList();
        p.printAtom("CJUMP");
        c.cond.accept(this);
        p.printAtom(c.trueLabel);
        if (c.hasFalseLabel()) {
            p.printAtom(c.falseLabel);
        }
        p.endList();
		return null;
	}

	public Void visit(IRJump j) {
        p.startList();
        p.printAtom("JUMP");
        j.target.accept(this);
        p.endList();
		return null;
	}
	
	public Void visit(IRCompUnit c) {
        p.startList();
        p.printAtom("COMPUNIT");
        p.printAtom(c.name);
        for (IRFuncDecl fn : c.functions.values()) {
            fn.accept(this);
        }
        p.endList();
		return null;
	}

	public Void visit(IRConst c) {
        p.startList();
        p.printAtom("CONST");
        p.printAtom(String.valueOf(c.value));
        p.endList();
		return null;
	}

	public Void visit(IRESeq e) {
        p.startList();
        p.printAtom("ESEQ");
        e.stmt.accept(this);
        e.expr.accept(this);
        p.endList();
		return null;
	}

	public Void visit(IRExp e) {
        p.startList();
        p.printAtom("EXP");
        e.expr.accept(this);
        p.endList();
		return null;
	}

	public Void visit(IRFuncDecl f) {
        p.startList();
        p.printAtom("FUNC");
        p.printAtom(f.name);
        f.body.accept(this);
        p.endList();
		return null;
	}

	public Void visit(IRLabel l) {
        p.startList();
        p.printAtom("LABEL");
        p.printAtom(l.name);
        p.endList();
		return null;
	}

	public Void visit(IRMem m) {
        p.startList();
        p.printAtom(m.memType.toString());
        m.expr.accept(this);
        p.endList();
		return null;
	}

	public Void visit(IRMove m) {
        p.startList();
        p.printAtom("MOVE");
        m.target.accept(this);
        m.src.accept(this);
        p.endList();
		return null;
	}

	public Void visit(IRName n) {
        p.startList();
        p.printAtom("NAME");
        p.printAtom(n.name);
        p.endList();
		return null;
	}

	public Void visit(IRReturn r) {
        p.startList();
        p.printAtom("RETURN");
        for (IRNode ret : r.rets) {
            ret.accept(this);
        }
        p.endList();
		return null;
	}

	public Void visit(IRSeq s) {
        p.startUnifiedList();
        p.printAtom("SEQ");
        for (IRNode stmt : s.stmts) {
            stmt.accept(this);
        }
        p.endList();
		return null;
	}

	public Void visit(IRTemp t) {
        p.startList();
        p.printAtom("TEMP");
        p.printAtom(t.name);
        p.endList();
		return null;
	}
}