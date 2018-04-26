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
import optimize.graph.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicInternalException;
public class CSEWorklist {

    /*
     * Helper method to return boolean if IRExpr contains a IRTemp from kill set
     */
    
    public boolean kill(Set<IRExpr> killSet, IRExpr e) {
        if (e instanceof IRTemp && killSet.contains(e)) {
            System.out.println("Kill e: " + e);
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

    /*
     * Given an IR statement, calculate the corresponding out
     */
    public Map<IRExpr, IRStmt> transfer(IRStmt s) {

        // Adding in to out
        Map<IRExpr, IRStmt> out = new HashMap<IRExpr, IRStmt>(s.CSEin);

        // Adding gen to out
        // TODO This is the wrong equals
        for (IRExpr e : s.exprs) {
            System.out.println("Expr at line 70:" + e);
            if (!out.containsKey(e)) {
                out.put(e, s);
            }
        }

        System.out.println("Transfer size MAX: " + out.size());

        // Kill mems if moving into mem or calling a function
        boolean shouldDelMem = false;
        if (s instanceof IRMove && ((((IRMove) s).target instanceof IRMem) || containsCall(((IRMove) s).src))) {
            shouldDelMem = true;        
        }

        // Performing kill for out
        Map<IRExpr, IRStmt> tempOut = new HashMap<IRExpr, IRStmt>(out);
        for (IRExpr e : tempOut.keySet()) {
            System.out.println("Expr: " + e);
            System.out.println("Kill: " + s.kill.size());
            if ((shouldDelMem && s.delMem) || kill(s.kill, e)) {
                out.remove(e);
                System.out.println("Remove");
            }
        }

        System.out.println("Transfer size: " + out.size());

        return out;
    }

    public void meet(IRGraph<Map<IRExpr, IRStmt>> g, IRStmt v) {        
        
        Map<IRExpr, IRStmt> in = null;
        
        for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> edge: g.incomingEdgesOf(v)) {
            Map<IRExpr, IRStmt> s = edge.value;
            // only want to intersect if s has been initialized
            if (s != null) {
                // if in has not been initialized, put all of the non-empty set's objects into it
                if (in == null) {
                    in = new HashMap<IRExpr, IRStmt>();
                    in.putAll(s);
                } else {
                    for (IRExpr e : new HashSet<IRExpr>(in.keySet())) {
                        if (!s.containsKey(e) || !in.get(e).equals(s.get(e))) {
                            in.remove(e);
                        } 
                    }
                    
                }
            }
        }

        // If it is the start node, initialize to empty
        if (in == null) {
            v.CSEin = new HashMap<IRExpr,IRStmt>();
        }
        else {
            v.CSEin = in;
        }

    }

    /*
     * Annotate the tree edges with the correct "out" for source's edge
     */
    public void annotate(IRGraph<Map<IRExpr, IRStmt>> g) {
        Set<IRStmt> vertices = g.vertexSet();
        
        Queue<IRStmt> w = new LinkedList<IRStmt>();
        w.add(g.start);

        System.out.println("New Annotate -------------------------");
        while (!w.isEmpty()) {
            IRStmt v = w.remove();
            System.out.println("Current Statment: " + v.toString().replace("\n",""));
            
            Map<IRExpr, IRStmt> oldIn = null;
            if (v.CSEin != null) {
                oldIn = new HashMap<>(v.CSEin); // Old CSEin
            }
            meet(g, v);                         // New CSEin

            boolean hasChanged = false;
            // If not intialized, don't terminate
            if (oldIn == null) {
                hasChanged = true;
            }
            // Otherwise, check if old CSEin == new CSEin (termination condition)
            else {
                for(IRExpr e: v.CSEin.keySet()) {
                    if (!(oldIn.keySet().contains(e) && oldIn.get(e).equals(v.CSEin.get(e)))) {
                        System.out.println("Things changed!");
                        hasChanged = true;
                    }
                }
            }

            // Continue analysis if things changed
            if (hasChanged) {
                Map<IRExpr, IRStmt> out = transfer(v);
                for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : g.outgoingEdgesOf(v)) {
                    System.out.println("Changing e.value");
                    e.value = out;
                    w.add(e.tail);
                }
            }
        }
    }


    /*
     * CSE optimization 
     */
    public void cse(IRGraph<Map<IRExpr, IRStmt>> g) {

        annotate(g);

        System.out.println("Annotation is done ------------------------------");

        // Temp names for exprs that are common subexprs
        Map<IRExpr, IRTemp> assigned = new HashMap<IRExpr, IRTemp>();
        Integer varCount = 0;

        ArrayList<IRStmt> seen = new ArrayList<IRStmt>();
        Queue<IRStmt> q = new LinkedList<IRStmt>();

        CSEReplaceVisitor replVisit = new CSEReplaceVisitor();
        
        q.add(g.start);
        while (!q.isEmpty()) {
            IRStmt s = q.poll();
            System.out.println("----- Current statment: " + s);
            // if (s instanceof IRSeq) {
            //     for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : g.outgoingEdgesOf(s)) {
            //         if (!seen.contains(g.getEdgeTarget(e))) {
            //             q.add(g.getEdgeTarget(e));
            //         }
                    
            //     }
            //     break;
            // }

            seen.add(s);
            PriorityQueue<IRExpr> orderedExprs = new PriorityQueue<IRExpr>(new ExprComparator());
            
            for (IRExpr e : s.exprs) {
                orderedExprs.add(e);
            }
            IRExpr cur = orderedExprs.poll();

            System.out.println("CSEin Keyset Size: " + s.CSEin.keySet().size());

            boolean alreadyRepl = false;
            // Iterate and pop orderedExprs
            while (cur != null && !alreadyRepl) {
                System.out.println("Current Expr: " + cur);
                // If current expression was ALREADY used
                if (assigned.containsKey(cur)) {
                    System.out.println("Current expr has been replaced");
                    replVisit.replaceExpr = cur;
                    replVisit.newExpr = assigned.get(cur);
                    s.accept(replVisit);
                    alreadyRepl = true;
                // If current expression can be replaced (and cur is not a temp)
                } else if (s.CSEin.containsKey(cur) && !(cur instanceof IRTemp)) {                    
                    System.out.println("Current expr can be replaced");
                    // look at mapped assignment
                    IRStmt node = s.CSEin.get(cur);
                    IRTemp newTemp = new IRTemp("_cse_" + varCount.toString());
                    assigned.put(cur, newTemp);

                    IRStmt newStmt = new IRMove(newTemp, cur);  // New statement to insert

                    varCount++;

                    g.addVertex(newStmt);

                    Set<PairEdge<IRStmt, Map<IRExpr, IRStmt>>> incoming = new HashSet<>(g.incomingEdgesOf(node));
                    // Editing the graph
                    for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : incoming) {
                        g.addEdge(e.head, newStmt);
                    }

                    g.removeAllEdges(incoming);
                    g.addEdge(newStmt, node);
                    
                    replVisit.replaceExpr = cur;
                    replVisit.newExpr = newTemp;
                    s.accept(replVisit);
                    node.accept(replVisit);

                    alreadyRepl = true;

                } 

                cur = orderedExprs.poll();
            }

            // killing any keys in assigned that were killed at this statement
            for (IRExpr a : new HashSet<IRExpr>(assigned.keySet())) {
                if (kill(s.kill, a)) {
                    assigned.remove(a);
                }
            }

            // adding all outgoing edges of node if not yet visited
            for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : g.outgoingEdgesOf(s)) {
                if (!seen.contains(g.getEdgeTarget(e))) {
                    q.add(g.getEdgeTarget(e));
                }
            }
        }
    }
}