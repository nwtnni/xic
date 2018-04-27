package optimize.cse;

import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.PriorityQueue;

import ir.*;
import optimize.Worklist;
import optimize.graph.*;
import util.PairEdge;

/** 
 * Worklist for available expressions analysis and perform CSE.
 */
public class CSEWorklist extends Worklist<IRGraph<Map<IRExpr, IRStmt>>, IRStmt, Map<IRExpr, IRStmt>> {

    public CSEWorklist(IRGraph<Map<IRExpr, IRStmt>> cfg) {
        super(cfg, Direction.FORWARD);
    }

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

    // TODO: remove this as this should be unnecessary with lowered IR?
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
     * using transfer function for CSE analysis
     */
    @Override
    public Map<IRExpr, IRStmt> transfer(Map<IRExpr, IRStmt> in, IRStmt s) {

        // Adding in to out
        Map<IRExpr, IRStmt> out = new HashMap<>(in);

        // Adding gen to out
        for (IRExpr e : s.exprs) {
            if (!out.containsKey(e)) {
                out.put(e, s);
            }
        }

        // Kill mems if moving into mem or calling a function
        boolean shouldDelMem = false;
        if (s instanceof IRMove && ((((IRMove) s).target instanceof IRMem) || containsCall(((IRMove) s).src))) {
            shouldDelMem = true;        
        }

        // Performing kill for out
        for (IRExpr e : new HashSet<>(out.keySet())) {
            if ((shouldDelMem && s.delMem) || kill(s.kill, e)) {
                out.remove(e);
            }
        }

        return out;
    }

    /*
     * Perform the meet of all outs of a node's predecessors 
     * which is the intersection of these sets
     *
     * Checks for equality of two expressions using the common subexpression and IRStmt that defined it
     */
    @Override
    public Map<IRExpr, IRStmt> meet(Set<PairEdge<IRStmt, Map<IRExpr, IRStmt>>> paths) {        
        
        Map<IRExpr, IRStmt> in = null;
        
        for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> edge : paths) {
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

        // If it is the start node, initialize to empty, else assign to CSEin
        if (in == null) {
            in = new HashMap<IRExpr,IRStmt>();
        }
        return in;
    }

    /**
     * Annotates the node with the updated in set of available expressions.
     */
    @Override
    public boolean annotate(IRStmt v, Map<IRExpr, IRStmt> in, Map<IRExpr, IRStmt> out) {
        boolean hasChanged = false;
        if (v.CSEin == null) {
            hasChanged = true;
        } else if (v.CSEin.size() != in.size()) {
            hasChanged = true;
        } else {
            Set<IRExpr> oldExprs = v.CSEin.keySet();
            Set<IRExpr> newExprs = in.keySet();
            hasChanged = !(oldExprs.containsAll(newExprs) && newExprs.containsAll(oldExprs));
        }

        v.CSEin = in;
        return hasChanged;
    }

    /*
     * Run the CSE optimization on graph [g]
     */
    public void runCSE() {

        // annotate(g);
        doWorklist();

        // Temp names for exprs that are common subexprs
        Map<IRExpr, IRTemp> assigned = new HashMap<IRExpr, IRTemp>();
        Integer varCount = 0;

        ArrayList<IRStmt> seen = new ArrayList<IRStmt>();
        Queue<IRStmt> q = new LinkedList<IRStmt>();

        CSEReplaceVisitor replVisit = new CSEReplaceVisitor();
        
        q.add(graph.start);
        while (!q.isEmpty()) {
            IRStmt s = q.poll();

            seen.add(s);
            PriorityQueue<IRExpr> orderedExprs = new PriorityQueue<IRExpr>(new ExprComparator());
            
            for (IRExpr e : s.exprs) {
                orderedExprs.add(e);
            }
            IRExpr cur = orderedExprs.poll();


            boolean alreadyRepl = false;
            // Iterate and pop orderedExprs
            while (cur != null && !alreadyRepl) {
                // If current expression was ALREADY used
                if (assigned.containsKey(cur)) {
                    replVisit.replaceExpr = cur;
                    replVisit.newExpr = assigned.get(cur);
                    s.accept(replVisit);
                    alreadyRepl = true;
                // If current expression can be replaced (and cur is not a temp)
                } else if (s.CSEin.containsKey(cur) && !(cur instanceof IRTemp)) {                    
                    // look at mapped assignment
                    IRStmt node = s.CSEin.get(cur);
                    IRTemp newTemp = new IRTemp("_cse_" + varCount.toString());
                    assigned.put(cur, newTemp);

                    IRStmt newStmt = new IRMove(newTemp, cur);  // New statement to insert

                    varCount++;

                    graph.addVertex(newStmt);

                    Set<PairEdge<IRStmt, Map<IRExpr, IRStmt>>> incoming = new HashSet<>(graph.incomingEdgesOf(node));
                    // Editing the graph
                    for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : incoming) {
                        graph.addEdge(e.head, newStmt);
                    }
                    graph.removeAllEdges(incoming);
                    graph.addEdge(newStmt, node);
                    
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
            for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : graph.outgoingEdgesOf(s)) {
                if (!seen.contains(graph.getEdgeTarget(e))) {
                    q.add(graph.getEdgeTarget(e));
                }
            }
        }
    }
}