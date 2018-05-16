package parse;

import java.util.List;
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
     * Psuedo-visit method for visiting a list of nodes.
     */
    @Override
    public List<MultipleNode> visit(List<Node> nodes) throws XicException {
        List<MultipleNode> t = new ArrayList<>();
        for (Node n : nodes) {
            t.add(n.accept(this));
        }
        return t;
    }

    /*
     * Top-level AST nodes
     */
    
    @Override
    public MultipleNode visit(XiProgram p) throws XicException {

        // Rewrite body
        List<Node> body = new ArrayList<>();
        for (Node declr : p.body) {
            MultipleNode n = declr.accept(this);
            if (n.isSingle()) {
                body.add(n.getSingle());
            } else {
                body.addAll(n.getMultiple());
            }
        }
        p.body = body;

        return null;
    }

    // Use statements are already desugared
    @Override
    public MultipleNode visit(XiUse u) throws XicException {
        return null;
    }

    // PA7
    @Override
    public MultipleNode visit(XiClass c) throws XicException {

        // Rewrite body
        List<Node> body = new ArrayList<>();
        for (Node declr : c.body) {
            MultipleNode n = declr.accept(this);

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

        // Rewrite block
        MultipleNode n = f.block.accept(this);
        f.block = n.getSingle();

        return MultipleNode.of(f);
    }

    // PA7
    @Override
    public MultipleNode visit(XiGlobal g) throws XicException {

        MultipleNode n = g.stmt.accept(this);
        if (n.isMultiple()) {
            List<Node> seq = new ArrayList<>();
            for (Node d : n.getMultiple()) {
                seq.add(new XiGlobal(d.location, d));
            }
            return MultipleNode.of(seq);
        } else {
            return MultipleNode.of(g);
        }
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
        return MultipleNode.of(i);
    }

    @Override
    public MultipleNode visit(XiReturn r) throws XicException {
        return MultipleNode.of(r);
    }

    // PA7
    // Grammar enforces no nested seqs 
    @Override
    public MultipleNode visit(XiSeq s) throws XicException {
        List<Node> seq = new ArrayList<>();
        for (Node n : s.stmts) {
            seq.add(n);
        }
        return MultipleNode.of(seq);
    }

    @Override
    public MultipleNode visit(XiWhile w) throws XicException {
        return MultipleNode.of(w);
    }

    // All expressions and constants are already desugared

}
