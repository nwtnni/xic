// package optimize.register;

// import java.util.List;
// import java.util.ArrayList;
// import java.util.Set;
// import java.util.HashSet;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.Stack;
// import java.util.Optional;

// import assemble.Temp;
// import assemble.Config;
// import assemble.Operand;
// import assemble.FuncDecl;
// import assemble.CompUnit;
// import assemble.instructions.*;
// import assemble.instructions.BinOp.Kind;

// import util.Either;

// import xic.XicInternalException;

// // TODO: refactor this like the trivial allocator
// public class Allocator extends InstrVisitor<Void> {

//     private static final Set<Operand> available = Set.of(
//         Operand.RAX,
//         Operand.RBX,
//         Operand.RCX,
//         Operand.RDX,
//         Operand.RSI,
//         Operand.RDI,
//         Operand.R8,
//         Operand.R9,
//         Operand.R10,
//         Operand.R11,
//         Operand.R12,
//         Operand.R13,
//         Operand.R14,
//         Operand.R15
//     );

//     public static CompUnit allocate(CompUnit unit) {
//         Allocator allocator = new Allocator(unit);
//         return allocator.allocate();
//     }

//     // Running list of assembly instructions
//     private CompUnit unit;

//     // Current list of instructions
//     private List<Instr> instrs;

//     // Map of named temps to registers
//     private Map<Temp, Operand> allocated;

//     // Number of temps on the stack - 1
//     private int tempCounter;

//     // Maximum number of args to a call in current function
//     private int maxArgs;

//     // Maximum number of returns from a call in current function
//     private int maxRets;

//     // Caller saved registers - not required for trivial allocation
//     // private Operand r10;
//     // private Operand r11;

//     private Allocator(CompUnit<Temp> unit) {
//         this.unit = unit;
//         this.instrs = new ArrayList<>();
//         this.tempCounter = 0;
//         this.maxArgs = 0;
//         this.maxRets = 0;
//         this.allocated = null;
//         // TODO do we need this
//         // this.isMultiple = 0;
//     }

//     private CompUnit<Reg> allocate() {
//         for (FuncDecl<Temp> fn : unit.fns) {
//             allocate(fn);
//         }
//         return null;
//     }

//     private void allocate(FuncDecl<Temp> fn) {

//         instrs = new ArrayList<>();
//         Set<Operand> saved = new HashSet<>();

//         tempCounter = 0;
//         maxArgs = 0;
//         maxRets = 0;
//         allocated = null;

//         // TODO loop and spill
//         Either<Temp, Map<Temp, Operand>> spilled = tryColor(fn);
//         assert spilled.isRight();
//         allocated = spilled.getRight();

//         for (Operand reg : allocated.values()) {
//             if (reg.isCalleeSaved() && !saved.contains(reg)) {
//                 saved.add(reg);
//                 fn.saveRegister(reg);
//             }
//         }

//         for (Instr i : fn.stmts) i.accept(this);
//         fn.stmts = instrs;

//         // Calculate words to shift rsp, +1 to offset tempCounter
//         int rsp = tempCounter + maxArgs + maxRets + 1;

//         // 16 byte alignment
//         rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
//         Operand shift = Operand.imm(rsp * Config.WORD_SIZE);

//         fn.setStackSize(rsp);

//         // // Insert stack setup
//         // BinOp sub = new BinOp(Kind.SUB, Operand.RSP, shift);
//         // fn.prelude.set(7, sub);

//         // // Insert stack teardown
//         // BinOp add = new BinOp(Kind.ADD, Operand.RSP, shift);
//         // fn.epilogue.set(2, add);
//     }

//     // Returns empty if colorable with spills
//     // Otherwise false and must spill the returned Temp
//     //
//     // Colors the provided ColorGraph
//     private Either<Temp, Map<Temp, Operand>> tryColor(FuncDecl fn) {

//         Stack<Temp> stack = new Stack<>();
//         InterferenceGraph interfere = new InterferenceGraph(fn.stmts, available.size());
//         ColorGraph color = new ColorGraph(fn.stmts, available);

//         while (interfere.size() > 0) {
//             interfere.pop().ifPresentOrElse(
//                 temp -> stack.push(temp),
//                 () -> stack.push(interfere.spill().get())
//             );
//         }

//         while (stack.size() > 0) {
//             Temp temp = stack.pop();
//             if (!color.tryColor(temp)) return Either.left(temp);
//         }

//         return Either.right(color.getColoring());
//     }

//     /* Recursive descent visitors */

//     public Void visit(BinOp op) {
//         assert op.dest == null && op.src == null;

//         if (isDead(op.destTemp) || isDead(op.srcTemp)) return null;

//         Operand dest = allocate(op.destTemp);
//         op.dest = dest;

//         Operand src = allocate(op.srcTemp);
//         // Insert mov when performing mem to mem or when imm > 32 bits
//         // TODO: add reg alloc for shift instructions
//         // Shift instructions only use imm8 or CL
//         if (src.isMem() && dest.isMem() || (src.isImm() && !Config.within(32, src.value()))) {
//             instrs.add(new Mov(Operand.RAX, src));
//             src = Operand.RAX;
//         }
//         op.src = src;

//         instrs.add(op);
//         return null;
//     }

//     public Void visit(Call call) {
//         maxArgs = Math.max(maxArgs, call.numArgs);
//         maxRets = Math.max(maxRets, call.numRet);
//         instrs.add(call);
//         return null;
//     }

//     public Void visit(Cmp cmp) {

//         if (isDead(cmp.leftTemp) || isDead(cmp.rightTemp)) return null;

//         cmp.left = allocate(cmp.leftTemp);
//         cmp.right = allocate(cmp.rightTemp);

//         instrs.add(cmp);
//         return null;
//     }

//     public Void visit(Cqo i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(DivMul op) {

//         if (isDead(op.destTemp) || isDead(op.srcTemp)) return null;

//         op.src = allocate(op.srcTemp);
//         op.dest = allocate(op.destTemp);

//         instrs.add(op);
//         return null;
//     }

//     public Void visit(Jcc i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(Jmp i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(Label i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(Lea lea) {

//         if (isDead(lea.destTemp) || isDead(lea.srcTemp)) return null;

//         lea.dest = allocate(lea.destTemp);
//         lea.src = allocate(lea.srcTemp);

//         instrs.add(lea);
//         return null;
//     }

//     public Void visit(Mov mov) {

//         if (isDead(mov.destTemp) || isDead(mov.srcTemp)) return null;

//         mov.src = allocate(mov.srcTemp);
//         mov.dest = allocate(mov.destTemp);

//         instrs.add(mov);
//         return null;
//     }

//     public Void visit(Pop i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(Push i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(Ret i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(Setcc i) {
//         instrs.add(i);
//         return null;
//     }

//     public Void visit(Text i) {
//         instrs.add(i);
//         return null;
//     }

//     private boolean isDead(Temp t) {
//         switch (t.kind) {
//             case TEMP:
//                 return !allocated.containsKey(t);

//             // Allocate a memory access off a base register
//             case MEM:
//             case MEMBR:
//                 assert t.base.isTemp() || t.base.isFixed() || t.base.isImm();
//                 return !allocated.containsKey(t.base);

//             // Allocate a memory access of a 2 registers with scale and an offset
//             case MEMSBR:
//                 assert t.base.isTemp() || t.base.isFixed() || t.base.isImm();
//                 assert t.reg.isTemp() || t.reg.isFixed() || t.reg.isImm();
//                 return !allocated.containsKey(t.base) || !allocated.containsKey(t.reg);

//             default:
//                 return false;
//         }
//     }

//     private Operand allocate(Temp t) {
//         switch (t.kind) {
//             // Allocate an immediate value
//             case IMM:
//                 return Operand.imm(t.value);

//             // Allocate an ordinary temporary
//             case TEMP:
//                 assert allocated.get(t) != null;
//                 return allocated.get(t);

//             // Allocate a memory access off a base register
//             case MEM:
//                 Operand base = allocate(t.base);
//                 if (base.isMem()) {
//                     instrs.add(new Mov(Operand.R11, allocate(t.base)));
//                     base = Operand.R11;
//                 }
//                 return Operand.mem(base);

//             // Allocate a memory access of a base register and offset
//             case MEMBR:
//                 base = allocate(t.base);
//                 if (base.isMem()) {
//                     instrs.add(new Mov(Operand.R11, allocate(t.base)));
//                     base = Operand.R11;
//                 }
//                 return Operand.mem(base, t.offset);

//             // Allocate a memory access of a 2 registers with scale and an offset
//             case MEMSBR:
//                 base = allocate(t.base);
//                 if (base.isMem()) {
//                     instrs.add(new Mov(Operand.R11, allocate(t.base)));
//                     base = Operand.R11;
//                 }
//                 Operand reg = allocate(t.reg);
//                 if (reg.isMem()) {
//                     instrs.add(new Mov(Operand.R10, allocate(t.reg)));
//                     reg = Operand.R10;
//                 }
//                 return Operand.mem(base, reg, t.offset, t.scale);

//             // Get the fixed register
//             case FIXED:
//                 return t.getRegister();
//         }

//         // Unreachable
//         assert false;
//         return null;
//     }
// }
