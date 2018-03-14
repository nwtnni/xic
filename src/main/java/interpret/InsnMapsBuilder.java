package interpret;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xic.XicException;
import ir.*;

public class InsnMapsBuilder extends IRVisitor<IRNode> {
    private Map<String, Long> nameToIndex;
    private Map<Long, IRNode> indexToInsn;
    private List<String> ctors;

    private long index;

    public InsnMapsBuilder() {
        nameToIndex = new HashMap<>();
        indexToInsn = new HashMap<>();
        ctors = new LinkedList<>();
        index = 0;
    }

    public Map<String, Long> nameToIndex() {
        return nameToIndex;
    }

    public Map<Long, IRNode> indexToInsn() {
        return indexToInsn;
    }

    public List<String> ctors() {
        return ctors;
    }

	public IRNode visit(IRCompUnit c) {
		return null;
	}

	public IRNode visit(IRFuncDecl f) {
		return null;
	}

	public IRNode visit(IRSeq s) {
		return null;
	}

	public IRNode visit(IRESeq e) {
		return null;
	}

	public IRNode visit(IRExp e) {
		return null;
	}

	public IRNode visit(IRCall c) {
		return null;
	}

	public IRNode visit(IRReturn r) {
		return null;
	}

	public IRNode visit(IRCJump c) {
		return null;
	}

	public IRNode visit(IRJump j) {
		return null;
	}
	
	public IRNode visit(IRName n) {
		return null;
	}

	public IRNode visit(IRLabel l) {
		return null;
	}

	public IRNode visit(IRTemp t) {
		return null;
	}
	
	public IRNode visit(IRMem m) {
		return null;
	}

	public IRNode visit(IRMove m) {
		return null;
	}

	public IRNode visit(IRBinOp b) {
		return null;
	}
	
	public IRNode visit(IRConst c) {
		return null;
	}

    public void addInsn(IRNode n) {
        indexToInsn.put(index, n);
        index++;
    }

    public void addNameToCurrentIndex(String name) throws XicException {
        if (nameToIndex.containsKey(name)) {
            throw XicException.internal("Error - encountered "
                    + "duplicate name " + name
                    + " in the IR tree -- go fix the generator.");
        }
        nameToIndex.put(name, index);
    }
}
