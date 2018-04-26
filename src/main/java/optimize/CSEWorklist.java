package optimize;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.UUID;


import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicInternalException;
public class CSEWorklist {

    /*
     * Helper method to return boolean if IRExpr contains a IRTemp from kill set
     */
    
    public boolean kill(Set<IRExpr> killSet, IRExpr e) {
        if (e instanceof IRTemp && killSet.contains(e)) {
            return true;
        }
        if (e instanceof IRBinOp) {
            IRBinOp b = (IRBinOp) e;
            return kill(killSet, b.left) || kill(killSet, b.right);
        }
        return false;
    } 

    /*
     * Helper method to determine if expression contains a call
     * Used to determine if IRMems should be removed from "out"
     */
    public boolean containsCall(IRExpr e) {
        if (e instanceof IRCall) {
            return true;
        }
        if (e instanceof IRTemp || e instanceof IRConst || e instanceof IRName) {
            return false;
        } 
        if (e instanceof IRBinOp) {
            IRBinOp b = (IRBinOp) e;
            return containsCall(b.right) || containsCall(b.left);
        }
        if (e instanceof IRMem) {
            return containsCall(((IRMem) e).expr);
        }
        return false;
    }

    public HashMap<IRExpr, IRStmt> transfer(HashMap<IRExpr, IRStmt> inSet, IRStmt s) {
        HashMap<IRExpr, IRStmt> out = new HashMap<IRExpr, IRStmt>(inSet);
        HashMap<IRExpr, IRStmt> tempOut = new HashMap<IRExpr, IRStmt>(inSet);
        for (IRExpr e : s.exprs) {
            if (!out.containsKey(e)) {
                out.put(e, s);
            }
        }

        boolean shouldDelMem = false;
        if (s instanceof IRMove && (((IRMove) s).target instanceof IRMem) || containsCall(((IRMove) s).src)) {
            shouldDelMem = true;        
        }
        for (IRExpr e : tempOut.keySet()) {
            if ((shouldDelMem && s.delMem) || kill(s.kill, e)) {
                out.remove(e);
            }
        }

        return out;
    }

    public void meet(IRGraph<HashMap<IRExpr, IRStmt>> g, IRStmt v) {
        Set<PairEdge<IRStmt, HashMap<IRExpr, IRStmt>>> allOut = g.incomingEdgesOf(v);
        Iterator<PairEdge<IRStmt, HashMap<IRExpr, IRStmt>>> iterAllOut = allOut.iterator();
        HashMap<IRExpr, IRStmt> in = null;

        while (iterAllOut.hasNext()) {
            HashMap<IRExpr, IRStmt> s = iterAllOut.next().value;

            // only want to intersect if s has been initialized
            if (s != null) {
                // if in has not been initialized, put all of the non-empty set's objects into it
                if (in == null) {
                    in = new HashMap<IRExpr, IRStmt>();
                    in.putAll(s);
                } else {
                    for (IRExpr e : in.keySet()) {
                        if (!s.containsKey(e) || !in.get(e).equals(s.get(e))) {
                            in.remove(e);
                        } 
                    }
                    
                }
            }
        }

        v.CSEin = in;

    }

    /*
     * Annotate the tree edges with the correct "out" for source's edge
     */
    public void annotate(IRGraph<HashMap<IRExpr, IRStmt>> g) {
        Set<IRStmt> vertices = g.vertexSet();
        
        Queue<IRStmt> w = new LinkedList<IRStmt>();
        // HashMap<IRExpr, IRStmt> allExprs = new HashMap<IRExpr, IRStmt>();
        // for (IRStmt v : vertices) {
        //     w.add(v);
        //     for (IRStmt v: )
        //     allExprs.putAll(v.exprs);
        // }
        // for (IRStmt v : vertices) {
        //     for (PairEdge<IRStmt, HashMap<IRExpr, IRStmt>> e : g.incomingEdgesOf(v)) {
        //         e.value = new HashMap<IRExpr, IRStmt>(allExprs);
        //     }
        // }
        while (!w.isEmpty()) {
            IRStmt v = w.remove();
            meet(g, v);
            HashMap<IRExpr, IRStmt> out = transfer(v.CSEin, v);
            if (!(v.CSEin).equals(out)) {
                for (PairEdge<IRStmt, HashMap<IRExpr, IRStmt>> e : g.incomingEdgesOf(v)) {
                    w.add(e.head);
                }
            }
        }
    }


    /*
     * CSE optimization 
     */
    public void cse(IRGraph<Map<IRExpr, IRStmt>> g) {
        // Temp names for exprs that are common subexprs
        Map<IRExpr, IRTemp> assigned = new HashMap<IRExpr, IRTemp>();
        Integer varCount = 0;

        ArrayList<IRStmt> seen = new ArrayList<IRStmt>();
        Queue<IRStmt> q = new LinkedList<IRStmt>();

        CSEReplaceVisitor replVisit = new CSEReplaceVisitor();
        
        q.add(g.start);
        while (!q.isEmpty()) {
            IRStmt s = q.poll();
            if (s instanceof IRSeq) {
                for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : g.outgoingEdgesOf(s)) {
                    if (!seen.contains(g.getEdgeTarget(e))) {
                        q.add(g.getEdgeTarget(e));
                    }
                    
                }
                break;
            }

            seen.add(s);
            PriorityQueue<IRExpr> orderedExprs = new PriorityQueue<IRExpr>(new ExprComparator());
            
            for (IRExpr e : s.exprs) {
                orderedExprs.add(e);
            }
            IRExpr cur = orderedExprs.poll();
            while (cur != null) {
                if (assigned.containsKey(cur)) {
                    replVisit.replaceExpr = cur;
                    replVisit.newExpr = assigned.get(cur);
                    s.accept(replVisit);
                    break;
                } else if (s.CSEin.containsKey(cur) && !(cur instanceof IRTemp)) {
                    // look at mapped assignment
                    IRStmt node = s.CSEin.get(cur);
                    IRTemp newTemp = new IRTemp("_cse_" + varCount.toString());
                    assigned.put(cur, newTemp);

                    IRStmt newStmt = new IRMove(newTemp, cur);

                    varCount++;

                    g.addVertex(newStmt);

                    for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : g.incomingEdgesOf(node)) {
                        g.addEdge(e.head, newStmt);
                    }
                    g.removeAllEdges(g.incomingEdgesOf(node));
                    g.addEdge(node, newStmt);
                    
                    replVisit.replaceExpr = cur;
                    replVisit.newExpr = newTemp;
                    s.accept(replVisit);
                    node.accept(replVisit);

                    break;

                }

                cur = orderedExprs.poll();
            }

            for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : g.outgoingEdgesOf(s)) {
                if (!seen.contains(g.getEdgeTarget(e))) {
                    q.add(g.getEdgeTarget(e));
                }
            }
        }
    }
}




