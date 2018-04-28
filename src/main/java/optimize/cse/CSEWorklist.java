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
import util.Pair;

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
            // System.out.println("I killed inside kill func");

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
            // System.out.println("killing a call: " + e);
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

        System.out.println("Exprs of stmt " + s + " -------------------------");
        for (IRExpr e : s.exprs) {
            System.out.println(e);
        }
        System.out.println("END OF Exprs of stmt s -------------------------------");

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


        // for (IRExpr e : s.kill) {
        //     System.out.println("Kill this temp: " + e);
        // }
        // System.out.println("Should Delete Mem is: " + shouldDelMem);


        // Performing kill for out
        for (IRExpr e : new HashSet<>(out.keySet())) {
            if ((shouldDelMem && s.delMem) || kill(s.kill, e)) {
                out.remove(e);
                System.out.println("Removing: " + e);
            }
        }

        System.out.println("BEGINNING OF TRANSFER SET of " + s + "-------------------------");
        for (IRExpr e : out.keySet()) {
            System.out.println(e);
        }
        System.out.println("END OF TRANSFER SET-------------------------------");




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
        for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> path : paths) {
            System.out.println("There is an edge from: " + path.head);
        }
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

        System.out.println("BEGINNING OF CSEIN SET-------------------------");
        for (IRExpr e : in.keySet()) {
            System.out.println(e);
        }
        System.out.println("END OF CSEIN SET-------------------------------");
        return in;
    }

    /**
     * Annotates the node with the updated in set of available expressions.
     */
    @Override
    public boolean annotate(IRStmt v, Map<IRExpr, IRStmt> in, Map<IRExpr, IRStmt> out) {
        Map<IRExpr, IRStmt> oldExprs = v.CSEin;
        Map<IRExpr, IRStmt> newExprs = in;

        boolean hasChanged = false;
        if (oldExprs == null) {
            hasChanged = true;
        } else if (oldExprs.size() != newExprs.size()) {
            hasChanged = true;
        } else {
            for(IRExpr e: newExprs.keySet()) {
                if (!(oldExprs.keySet().contains(e) && oldExprs.get(e).equals(newExprs.get(e)))) {
                    hasChanged = true;
                }
            }
        }

        v.CSEin = in;
        System.out.println("BEGINNING OF actual csein SET-------------------------");
        for (IRExpr e : v.CSEin.keySet()) {
            System.out.println(e);
        }
        System.out.println("END OF actual csein SET-------------------------------");
        System.out.println("have i changed? " + hasChanged);
        return hasChanged;
    }

    /*
     * Run the CSE optimization on graph [g]
     */
    public void runCSE() {

        // annotate(g);
        doWorklist();

        // Temp names for exprs that are common subexprs
        Map<Pair<IRExpr, IRStmt> , IRTemp> assigned = new HashMap<Pair<IRExpr, IRStmt> , IRTemp>();

        Integer varCount = 0;

        ArrayList<IRStmt> seen = new ArrayList<IRStmt>();
        Queue<IRStmt> q = new LinkedList<IRStmt>();

        CSEReplaceVisitor replVisit = new CSEReplaceVisitor();
        
        q.add(graph.start);
        while (!q.isEmpty()) {
            IRStmt s = q.poll();
            System.out.println("Running CSE on this statment: " + s);

            System.out.println("Assigned keys -------------------------");
            for (Pair<IRExpr, IRStmt> e : assigned.keySet()) {
                System.out.println("The expr: " + e.first + " from:" + e.second);
            }
            System.out.println("END OF Assigned keys-------------------------------");

            seen.add(s);
            PriorityQueue<IRExpr> orderedExprs = new PriorityQueue<IRExpr>(new ExprComparator());
            
            for (IRExpr e : s.exprs) {
                orderedExprs.add(e);
            }
            IRExpr cur = orderedExprs.poll();


            boolean alreadyRepl = false;
            // Iterate and pop orderedExprs
            while (cur != null && !alreadyRepl) {
                System.out.println("Expr: " + cur);
                IRStmt frm = null;
                Set<PairEdge<IRStmt, Map<IRExpr, IRStmt>>> v_out = graph.outgoingEdgesOf(s);
                if (v_out.size() > 0) {
                    Map<IRExpr, IRStmt> tempmap = null;
                    for (PairEdge<IRStmt, Map<IRExpr, IRStmt>> sout : v_out) {
                        tempmap = sout.value;
                        break;
                    }
                    frm = tempmap.get(cur);
                }
                // If current expression was ALREADY used
                if (assigned.containsKey(new Pair<IRExpr, IRStmt>(cur, frm))) {
                    System.out.println("Able to use a temp from the key");
                    replVisit.replaceExpr = cur;
                    replVisit.newExpr = assigned.get(cur);
                    s.accept(replVisit);
                    alreadyRepl = true;
                // If current expression can be replaced (and cur is not a temp)
                } else if (s.CSEin.containsKey(cur) && !(cur instanceof IRTemp)) {     
                    // look at mapped assignment
                    IRStmt node = s.CSEin.get(cur);
                    IRTemp newTemp = new IRTemp("_cse_" + varCount.toString());
                    assigned.put(new Pair<IRExpr, IRStmt>(cur, node), newTemp);

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

            System.out.println("KILL SET -------------------------");
            for (IRExpr e : s.kill) {
                System.out.println(e);
            }
            System.out.println("END OF KILL SET-------------------------------");
            // killing any keys in assigned that were killed at this statement
            for (Pair<IRExpr, IRStmt> a : new HashSet<Pair<IRExpr, IRStmt>>(assigned.keySet())) {
                if (kill(s.kill, a.first)) {
                    assigned.remove(a);
                    System.out.println("removing from assigned: " + a);
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