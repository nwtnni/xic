package parser;

import java.util.ArrayList;

public abstract class Node {

    protected ArrayList<Node> children;
    protected ArrayList<Type> type;

    // S-expression visualization of this subtree
    public abstract String toString();

}
