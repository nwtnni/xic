package parser;

import java.util.ArrayList;

public abstract class Node {
    
    public Type type;

    public abstract void accept(Visitor v);

}
