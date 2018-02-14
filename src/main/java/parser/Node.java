package parser;

import java.util.ArrayList;

public abstract class Node {
    
    public abstract void accept(Visitor v);

}
