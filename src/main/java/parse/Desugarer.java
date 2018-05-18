package parse;

import java.util.List;
import java.util.stream.Collectors;
import java.awt.font.MultipleMaster;
import java.util.ArrayList;

import ast.*;
import xic.XicException;

/** Removes syntactic sugar from AST. */
public class Desugarer extends ASTVisitor<MultipleNode> {

    /**
     * Factory method to desugar a given AST.
     */
    public static void desugar(Node ast) throws XicException {
        ast.accept(new Desugarer());
    }

    /*
     * Top-level AST nodes
     */
    
    @Override
    public MultipleNode visit(XiProgram p) throws XicException {

        // Visit each declaration in body
        List<MultipleNode> declrs = visit(p.body);

        // Rewrite body
        List<Node> body = new ArrayList<>();
        for (MultipleNode n : declrs) {
            n.match(
                single -> body.add(single),
                multiple -> body.addAll(multiple)
            );
        }
        p.body = body;

        return null;
    }

    // Use statements don't need to be desugared
    @Override
    public MultipleNode visit(XiUse u) throws XicException {
        return null;
    }

    // PA7
    @Override
    public MultipleNode visit(XiClass c) throws XicException {

        // Visit each member of the class
        List<MultipleNode> members = visit(c.body);

        // Rewrite body
        List<Node> body = new ArrayList<>();
        for (MultipleNode n : members) {
            n.match(
                single -> body.add(single),
                multiple -> body.addAll(multiple)
            );
        }
        c.body = body;

        return MultipleNode.of(c);
    }

    // PA7
    @Override
    public MultipleNode visit(XiFn f) throws XicException {

        // Rewrite block for function definitions
        if (f.isDef()) {
            f.block = f.block.accept(this).getSingle();
        }

        return MultipleNode.of(f);
    }

    // PA7
    @Override
    public MultipleNode visit(XiGlobal g) throws XicException {

        // Visit statement
        MultipleNode n = g.stmt.accept(this);
        
        // Return multiple if found
        return n.match(
            single -> n,
            multiple -> { 
                List<Node> seq = new ArrayList<>();
                for (Node d : multiple) {
                    seq.add(new XiGlobal(d.location, d));
                }
                return MultipleNode.of(seq); 
            }
        );
    }

    /*
     * Statement nodes
     */

    @Override
    public MultipleNode visit(XiAssign a) throws XicException {
        return MultipleNode.of(a);
    }

    @Override
    public MultipleNode visit(XiBlock b) throws XicException {

        // Visit each statement
        List<MultipleNode> stmts = visit(b.statements);

        // Rewrite block
        List<Node> block = new ArrayList<>();
        for (MultipleNode n : stmts) {
            n.match(
                single -> block.add(single), 
                multiple -> block.addAll(multiple)
            );
        }
        b.statements = block;

        return MultipleNode.of(b);
    }

    // PA7
    @Override
    public MultipleNode visit(XiBreak b) throws XicException {
        return MultipleNode.of(b);
    }

    @Override
    public MultipleNode visit(XiDeclr d) throws XicException {
        return MultipleNode.of(d);
    }

    @Override
    public MultipleNode visit(XiIf i) throws XicException {

        // Rewrite if block
        i.block = i.block.accept(this).match(
            single -> single, 
            multiple -> new XiBlock(multiple)
        );

        // Rewrite else block if needed
        if (i.hasElse()) {
            i.elseBlock = i.elseBlock.accept(this).match(
                single -> single, 
                multiple -> new XiBlock(multiple)
            );
        }

        return MultipleNode.of(i);
    }

    @Override
    public MultipleNode visit(XiReturn r) throws XicException {
        return MultipleNode.of(r);
    }

    // PA7
    @Override
    public MultipleNode visit(XiSeq s) throws XicException {

        // Grammar enforces no nested seqs so no need to visit;
        return MultipleNode.of(s.stmts);
    }

    @Override
    public MultipleNode visit(XiWhile w) throws XicException {

        // Rewrite while block
        w.block = w.block.accept(this).match(
            single -> single, 
            multiple -> new XiBlock(multiple)
        );

        return MultipleNode.of(w);
    }

    /* All expressions and constants do not require desugaring */

}
