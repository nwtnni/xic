package emit;

import java.util.List;
import java.util.ArrayList;

import ast.*;
import ir.*;
import ir.IRBinOp.OpType;
import ir.IRMem.MemType;
import interpret.Configuration;
import xic.XicException;

public class Library {

    /*
     * Configuration constants.
     */

    protected static final IRConst WORD_SIZE = new IRConst(Configuration.WORD_SIZE);
    protected static final IRConst ZERO = new IRConst(0);
    protected static final IRConst ONE = new IRConst(1);

    /** 
     * ABI names for array library functions ignore the types of arrays and 
     * treat each argument as a 64-bit pointer (equivalent to an integer)
     */
    protected static final String ARRAY_ALLOC = "_xi_d_alloc";
    protected static final String ARRAY_CONCAT = "_xi_array_concat";

    /* 
     * Utility methods for code generation
     */

    /**
     * Make a jump to a label.
     */
    protected static IRJump jump(IRLabel l) {
        return new IRJump(l);
    }

    /**
     * Generate a loop in IR code given a IR node guard and body.
     */
    protected static IRStmt generateLoop(String name, IRExpr guard, IRStmt block) {
        IRLabel headL = IRLabelFactory.generate(name);
        IRLabel trueL = IRLabelFactory.generate("true");
        IRLabel falseL = IRLabelFactory.generate("false");

        return new IRSeq(
            headL,
            new IRCJump(guard, trueL),
            jump(falseL),
            trueL,
            block,
            jump(headL),
            falseL
        );
    }

    /**
     * Increment pointer by WORD_SIZE bytes.
     */
    protected static IRStmt incrPointer(IRTemp pointer) {
        IRExpr addr = new IRBinOp(OpType.ADD, pointer, WORD_SIZE);
        return new IRMove(pointer, addr);
    }

    /**
     * Increments a temp by 1.
     */
    protected static IRStmt increment(IRTemp i) {
        IRExpr plus = new IRBinOp(IRBinOp.OpType.ADD, i, ONE);
        return new IRMove(i, plus);
    }

    /**
     * Allocate memory for an array and copy the values into memory.
     */
    protected static IRExpr alloc(List<IRNode> array) throws XicException {
        IRSeq stmts = new IRSeq();
        
        // Calcuate size of array
        int length = array.size();
        IRConst size = new IRConst((length + 1) * Configuration.WORD_SIZE);
        
        // Generate pointers and allocate memory
        IRExpr addr =  new IRCall(new IRName("_xi_alloc"), size);
        IRTemp pointer = IRTempFactory.generate("array");
        stmts.add(new IRMove(pointer, addr));

        //Store length of array
        stmts.add(new IRMove(new IRMem(pointer, MemType.IMMUTABLE), new IRConst(length)));

        // Storing values of array into memory
        for(int i = 0; i < length; i++) {
            IRExpr n = (IRExpr) array.get(i);

            // index = j(workpointer)
            IRConst offset = new IRConst((i + 1) * WORD_SIZE.value()); 
            IRExpr index = new IRBinOp(OpType.ADD, pointer, offset);
            IRMem elem = new IRMem(index, MemType.IMMUTABLE);
            
            stmts.add(new IRMove(elem, n));
        }

        // Shift pointer to head of array
        stmts.add(incrPointer(pointer));

        return new IRESeq(
            stmts, 
            pointer,
            array
        );
    }

    /**
     * Allocate memory for a string.
     */
    protected static IRExpr alloc(XiString s) throws XicException {
        List<IRNode> chars = new ArrayList<>();
        for (Long c : s.value) {
            chars.add(new IRConst(c));
        }
        return alloc(chars);
    }

    /**
     * Dynamically allocate memory for an an array of size length
     */
    protected static IRExpr alloc(IRExpr length) {
        return new IRCall(new IRName(ARRAY_ALLOC), length);
    }

    /**
     * Dynamically allocate memory for an array of length size and
     * populate each entry with a copy of child. 
     */
    protected static IRExpr populate(IRExpr size, IRExpr child) {
        IRSeq stmts = new IRSeq();

        // Generate pointers and allocate memory
        IRTemp pointer = IRTempFactory.generate("populate_array");
        stmts.add(new IRMove(pointer, alloc(size)));

        // Create copies of the child (so no checking if child is an alloc)
        // addr = (workPointer,i,8)
        IRTemp i = IRTempFactory.generate("i");
        IRExpr index = new IRBinOp(OpType.MUL, i, new IRConst(8));
        IRMem addr = new IRMem(new IRBinOp(OpType.ADD, pointer, index), MemType.IMMUTABLE);
        
        stmts.add(new IRMove(i, ZERO));
        stmts.add(generateLoop(
            "make_array_loop",
            new IRBinOp(OpType.LT, i, size),
            new IRSeq(
                new IRMove(addr, child),
                increment(i)
            )
        ));

        return new IRESeq(
            stmts, 
            pointer
        );
    }

    /**
     * Generate code for the length built-in function.
     */
    protected static IRExpr length(IRExpr pointer) {
        return new IRMem(new IRBinOp(OpType.SUB, pointer, WORD_SIZE));
    }

    /*
     * Library functions
     */

    /**
     * Generates library function for allocating memory for an dynamic array.
     */
    protected static IRFuncDecl xiDynamicAlloc() {
        IRFuncDecl fn = new IRFuncDecl(ARRAY_ALLOC, ARRAY_ALLOC);

        IRTemp length = IRTempFactory.generate("d_length");
        fn.add(new IRMove(length, IRTempFactory.getArgument(0)));

        // Calculate size of array
        IRExpr byteSize = new IRBinOp(
            OpType.MUL,
            WORD_SIZE,
            new IRBinOp(OpType.ADD, length, ONE)
        );

        // Generate pointers and allocate memory
        IRExpr addr =  new IRCall(new IRName("_xi_alloc"), byteSize);
        IRTemp pointer = IRTempFactory.generate("d_array");
        fn.add(new IRMove(pointer, addr));

        // Store length then shift pointer
        fn.add(new IRMove(new IRMem(pointer, MemType.IMMUTABLE), length));
        fn.add(incrPointer(pointer));

        fn.add(new IRReturn(pointer));

        return fn;
    }

    /**
     * Generates library function for accessing an array.
     * _xi_array_concat(a, b)
     */
    protected static IRFuncDecl xiArrayConcat() {
        IRFuncDecl fn = new IRFuncDecl(ARRAY_CONCAT, ARRAY_CONCAT);

        // Make copies of pointers
        IRTemp ap = IRTempFactory.generate("a");
        fn.add(new IRMove(ap, IRTempFactory.getArgument(0)));
        IRTemp bp = IRTempFactory.generate("b");
        fn.add(new IRMove(bp, IRTempFactory.getArgument(1)));

        // Calculate new array size
        IRExpr aLen = IRTempFactory.generate("aLen");
        fn.add(new IRMove(aLen, length(ap)));
        IRExpr bLen = IRTempFactory.generate("bLen");
        fn.add(new IRMove(bLen, length(bp)));
        IRTemp size = IRTempFactory.generate("size");
        fn.add(new IRMove(size, new IRBinOp(OpType.ADD, aLen, bLen)));

        // Generate pointers and allocate memory
        IRTemp pointer = IRTempFactory.generate("array");
        fn.add(new IRMove(pointer, alloc(size)));

        IRTemp i = IRTempFactory.generate("i");
        IRExpr index = new IRBinOp(OpType.MUL, i, new IRConst(8));
        IRMem addr = new IRMem(new IRBinOp(OpType.ADD, pointer, index), MemType.IMMUTABLE);
        IRMem aElem = new IRMem(new IRBinOp(OpType.ADD, ap, index), MemType.IMMUTABLE);
        
        fn.add(new IRMove(i, ZERO));
        fn.add(generateLoop(
            "copy_a_loop",
            new IRBinOp(OpType.LT, i, aLen), 
            new IRSeq(
                new IRMove(addr, aElem),
                increment(i)
            )
        ));

        IRTemp j = IRTempFactory.generate("j");
        IRExpr indexb = new IRBinOp(OpType.MUL, j, new IRConst(8));
        IRMem bElem = new IRMem(new IRBinOp(OpType.ADD, bp, indexb), MemType.IMMUTABLE);

        fn.add(new IRMove(j, ZERO));
        fn.add(generateLoop(
            "copy_b_loop",
            new IRBinOp(OpType.LT, j, bLen), 
            new IRSeq(
                new IRMove(addr, bElem),
                increment(i),
                increment(j)
            )
        ));

        fn.add(new IRReturn(pointer));

        return fn;
    }
}