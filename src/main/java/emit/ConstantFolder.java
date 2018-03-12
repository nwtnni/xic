package emit;

import java.util.OptionalLong;
import ir.*;

public class ConstantFolder extends IRVisitor<OptionalLong> {

    IRNode tree;

	public OptionalLong visit(IRBinOp b) {
		return null;
	}
	
	public OptionalLong visit(IRCall c) {
		return null;
	}

	public OptionalLong visit(IRCJump c) {
		return null;
	}

	public OptionalLong visit(IRJump j) {
		return null;
	}
	
	public OptionalLong visit(IRCompUnit c) {
		return null;
	}

	public OptionalLong visit(IRConst c) {
		return null;
	}

	public OptionalLong visit(IRESeq e) {
		return null;
	}

	public OptionalLong visit(IRExp e) {
		return null;
	}

	public OptionalLong visit(IRFuncDecl f) {
		return null;
	}

	public OptionalLong visit(IRLabel l) {
		return null;
	}

	public OptionalLong visit(IRMem m) {
		return null;
	}

	public OptionalLong visit(IRMove m) {
		return null;
	}

	public OptionalLong visit(IRName n) {
		return null;
	}

	public OptionalLong visit(IRReturn r) {
		return null;
	}

	public OptionalLong visit(IRSeq s) {
		return null;
	}

	public OptionalLong visit(IRTemp t) {
		return null;
	}
}