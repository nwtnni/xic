package emit; 
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.Test;
import ir.*;

public class ConstantFolderTest {

    IRConst c1 = new IRConst(1);
    IRConst c2 = new IRConst(2);
    IRConst c0 = new IRConst(0);
    IRBinOp b1 = new IRBinOp(IRBinOp.OpType.ADD, c1, c2);
    IRBinOp b2 = new IRBinOp(IRBinOp.OpType.EQ, c1, c2);
    
    ConstantFolder cf = new ConstantFolder();   

    @Test
    public void bin1() {
        assertEquals(cf.visit(b1).getAsLong(), 3);
    }
    @Test
    public void bin2() {
    		IRBinOp b = new IRBinOp(IRBinOp.OpType.DIV, c1, c0);
    		assertEquals(cf.visit(b), OptionalLong.empty());
    }
    @Test
    public void call1() {
    		IRCall c = new IRCall(new IRName("foo"), b1);
    		cf.visit(c);
    		Printer.debug(c);
    		assertEquals(((IRConst) c.get(0)).value(), 3);
    }
    @Test
    public void jump1() {
    		IRJump j = new IRJump(new IRBinOp(IRBinOp.OpType.DIV, new IRConst(300), new IRConst(3)));
    		cf.visit(j);
    		assertEquals(((IRConst) j.target()).value(), 100);
    }
    @Test
    public void cjump1() {
    		IRBinOp b3 = new IRBinOp(IRBinOp.OpType.NEQ, b2, b2);
    		IRCJump cj = new IRCJump(b3, new IRLabel("hi"), new IRLabel("hello"));
    		cf.visit(cj);
    		assertEquals(((IRConst) cj.cond).value(), 0);
    }
}	