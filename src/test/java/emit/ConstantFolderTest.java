package emit; 
import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.Test;
import ir.*;
public class ConstantFolderTest {
    
	

    IRConst c1 = new IRConst(1);
    IRConst c2 = new IRConst(2);
    IRConst c0 = new IRConst(0);
    
    ConstantFolder cf = new ConstantFolder();

    

    @Test
    public void bin1() {
    		IRBinOp b = new IRBinOp(IRBinOp.OpType.ADD, c1, c2);
        assertEquals(cf.visit(b).getAsLong(), 3);
    }
    @Test
    public void bin2() {
    		IRBinOp b = new IRBinOp(IRBinOp.OpType.DIV, c1, c0);
    		assertEquals(cf.visit(b), OptionalLong.empty());
    }
}	