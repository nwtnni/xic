package optimize.cse;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
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
import util.Pair;

/** 
 * Worklist for available expressions analysis and perform CSE optimization.
 */
public class CSEWorklist extends Worklist<IRGraph<Map<IRExpr, IRStmt>>, IRStmt, Map<IRExpr, IRStmt>> {

    public IRDeepCopier dc = new IRDeepCopier();

    public CSEWorklist(IRGraph<Map<IRExpr, IRStmt>> cfg) {
        super(cfg, Direction.FORWARD);
    }

    /**
     * Helper method to return boolean if IRExpr contains a IRTemp from kill set
     * @param killSet set of IRExprs that a IRStmt kills; normally a set of one IRTemp
     * @param e IRExpr is an expression recursively searched for the presence of a nested IRExpr in the killSet
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
    /**
     * Helper method to determine if expression contains a call
     * Used to determine if IRMems should be removed from "out"
     * @param e IRExpr being recursively searched for an IRCall
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

    /**
     * Given an IR statement, calculate the corresponding out
     * using transfer function for CSE analysis
     * @param in map for the CSEin of a node that maps expressions of that node to the statement it was generated
     * @param s IRStmt the node of the graph where in = CSEin
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
            if ((shouldDelMem && e.delMem) || kill(s.kill, e)) {
                out.remove(e);
            }
        }

        return out;
    }

    /**
     * Perform the meet of all outs of a node's predecessors 
     * which is the intersection of these sets
     *
     * Checks for equality of two expressions using the common subexpression and IRStmt that defined it
     * @param paths Set for all the maps on the edges going into a node of the graph; used to perform the meet for a node
     */
    @Override
    public Map<IRExpr, IRStmt> meet(Set<Map<IRExpr, IRStmt>> paths) {        

        Map<IRExpr, IRStmt> in = null;
        
        for (Map<IRExpr, IRStmt> s : paths) {
            // only want to intersect if s has been initialized
            if (s != null) {
                // Used to get the first set (to intersect with everything else)
                if (in == null) {
                    in = new HashMap<IRExpr, IRStmt>();
                    for (IRExpr e : s.keySet()) {
                        // Deep copy needed because we will mutate the IRExprs
                        in.put(e.accept(dc), s.get(e));
                    }
                } 
                // Keep only the intersections
                else {
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
     * @param v IRStmt that will be annotated with the new in map 
     * @param in map is the newly calculated in set for v
     * @param out map 
     */
    @Override
    public boolean annotate(IRStmt v, Map<IRExpr, IRStmt> in, Map<IRExpr, IRStmt> out) {
        Map<IRExpr, IRStmt> oldExprs = v.CSEin;
        Map<IRExpr, IRStmt> newExprs = in;

        boolean hasChanged = false;     // Used to detect termination

        // Continue if uninitialized or if newExprs has changed size or has been updated 
        if (oldExprs == null) {
            hasChanged = true;
        } else if (oldExprs.size() != newExprs.size()) {
            hasChanged = true;
        } else {
            for(IRExpr e: newExprs.keySet()) {
                if (!(oldExprs.containsKey(e) && oldExprs.get(e).equals(newExprs.get(e)))) {
                    hasChanged = true;
                }
            }
        }

        v.CSEin = in;   // Update CSEin (annotation step)
        return hasChanged;
    }

    /*
     * Run the CSE optimization on graph from superclass Worklist
     */
    public void runCSE() {
        // System.out.println("Run CSE");

        doWorklist();   // Run available expressions analysis

        // Temp names for exprs that are common subexprs
        Map<Pair<IRExpr, IRStmt>, IRTemp> tempMap = new HashMap<>();
        Integer varCount = 0;   // Used to name cse replacement temps

        Queue<IRStmt> q = new LinkedList<>();
        Set<IRStmt> seen = new HashSet<>();     // Has been added to q at some point.

        CSEReplaceVisitor replVisit = new CSEReplaceVisitor();

        // Map from a stmt to the (expr, defining location) that will be used for CSE
        Map<IRStmt, Pair<IRExpr, IRStmt>> stmtToExpr = new HashMap<>();
        
        // Iterate to mark nodes for replacement
        q.add(graph.start);
        seen.add(graph.start);

        // System.out.println("Iterating through queue");

        while (!q.isEmpty()) {
            IRStmt s = q.poll();

            PriorityQueue<IRExpr> orderedExprs = new PriorityQueue<IRExpr>(new ExprComparator());
            orderedExprs.addAll(s.exprs);
            
            IRExpr curExpr = orderedExprs.poll();

            // Iterate and pop orderedExprs
            while (curExpr != null) {
                // If current expression can be replaced (and cur is not a temp)
                if (s.CSEin.containsKey(curExpr) && !(curExpr instanceof IRTemp)) {    
                    IRStmt defNode = s.CSEin.get(curExpr);
                    Pair<IRExpr, IRStmt> curPair = new Pair<>(curExpr, defNode);
                    
                    // If this expression was not already replaced
                    if (!tempMap.containsKey(curPair)) {
                        // Generate temp to hold expression
                        IRTemp newTemp = new IRTemp("_cse_" + varCount.toString());
                        tempMap.put(curPair, newTemp);
                        varCount++;
                    }

                    ExprComparator cmp = new ExprComparator();
                    // Update s replacement if it would be bigger
                    if(!stmtToExpr.containsKey(s)
                        || cmp.sizeExpr(curExpr) > cmp.sizeExpr(stmtToExpr.get(s).first)) {
                        stmtToExpr.put(s, curPair);
                    }

                    // Update defNode replacement if it would be bigger
                    if(!stmtToExpr.containsKey(defNode) 
                        || cmp.sizeExpr(curExpr) > cmp.sizeExpr(stmtToExpr.get(defNode).first)) {
                        stmtToExpr.put(defNode, curPair);
                    }
                    break;
                } 

                curExpr = orderedExprs.poll();
            }

            // Add outgoing edges to q if they haven't been seen before
            for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : graph.outgoingEdgesOf(s)) {
                if (!seen.contains(e.tail)) {
                    seen.add(e.tail);
                    q.add(e.tail);
                }
            }
        }

        // System.out.println("Finished Queue");

        // Flip the stmtToExpr map to go from expr to a list of stmts
        Map<Pair<IRExpr, IRStmt>, List<IRStmt>> exprToStmts = new HashMap<>();
        for(IRStmt s:stmtToExpr.keySet()) {
            
            Pair<IRExpr,IRStmt> e = stmtToExpr.get(s);

            // Initialize list if making a new key
            if(!exprToStmts.containsKey(e)) {
                exprToStmts.put(e, new ArrayList<IRStmt>());
            }

            exprToStmts.get(e).add(s);  // Add s to the list of statments corresponding to e
        }

        // System.out.println("Replacing exprs");

        // Iterate over potential exprs to replace in the IRStmts
        for (Pair<IRExpr, IRStmt> curPair: exprToStmts.keySet()) {
            List<IRStmt> stmts = exprToStmts.get(curPair);
            if (stmts.size() > 1) {

                // Unfolding the curPair data
                IRExpr curExpr = curPair.first;
                IRStmt defNode = curPair.second;

                // Adding new move stmt before original computation point
                IRStmt newStmt = new IRMove(tempMap.get(curPair), curExpr);
                graph.addVertex(newStmt);

                // Editing the edges of the graph
                Set<PairEdge<IRStmt, Map<IRExpr, IRStmt>>> incoming = new HashSet<>(graph.incomingEdgesOf(defNode));
                for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> e : incoming) {
                    graph.addEdge(e.head, newStmt);
                }
                graph.removeAllEdges(incoming);
                graph.addEdge(newStmt, defNode);

                // Performing the replacements
                for(IRStmt stmt: stmts) {
                    replVisit.replaceExpr = curExpr;    // Expression to replace using replVisit
                    replVisit.newExpr = tempMap.get(curPair);    // Temp to replace the expression using replVisit

                    // NOTE: Not allowed to call CSEin.keySet() because mutations change keySet pointers
                    // However, accesses are still fine because hashmaps only care about hashcode
                    defNode.accept(replVisit);  // Update the original computation point
                    stmt.accept(replVisit);     // Update the current computation point

                }
            }
        }

        // System.out.println("Finished");
    }
}