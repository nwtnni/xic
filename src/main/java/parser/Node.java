package parser;

import java.util.ArrayList;

public abstract class Node {

    protected ArrayList<Node> children;

    // S-expression visualization of this subtree
    public abstract String toString();

    // Checks this subtree's invariants
    public abstract boolean validate();

}
