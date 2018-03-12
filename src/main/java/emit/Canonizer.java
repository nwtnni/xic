package emit;

import ir.*;

public class Canonizer extends IRVisitor<IRNode> {

	public IRNode visit(IRBinOp b) {
		return null;
	}
	
	public IRNode visit(IRCall c) {
		return null;
	}

	public IRNode visit(IRCJump c) {
		return null;
	}

	public IRNode visit(IRJump j) {
		return null;
	}
	
	public IRNode visit(IRCompUnit c) {
		return null;
	}

	public IRNode visit(IRConst c) {
		return null;
	}

	public IRNode visit(IRESeq e) {
		return null;
	}

	public IRNode visit(IRExp e) {
		return null;
	}

	public IRNode visit(IRFuncDecl f) {
		return null;
	}

	public IRNode visit(IRLabel l) {
		return null;
	}

	public IRNode visit(IRMem m) {
		return null;
	}

	public IRNode visit(IRMove m) {
		return null;
	}

	public IRNode visit(IRName n) {
		return null;
	}

	public IRNode visit(IRReturn r) {
		return null;
	}

	public IRNode visit(IRSeq s) {
		return null;
	}

	public IRNode visit(IRTemp t) {
		return null;
    }

}