package parser;

import java.util.ArrayList;

public class Function extends Node {
    
    private String id;
    private int ARGS_INDEX;
    private int TYPE_INDEX;
    private int BLOCK_INDEX;

    public Function(String id, ArrayList<Node> args, ArrayList<Node> types, Node block) {
        this.id = id;  
        this.ARGS_INDEX = 0;
        this.TYPE_INDEX = args.size();
        this.BLOCK_INDEX = args.size() + types.size();
        this.children.addAll(args);
        this.children.addAll(types);
        this.children.add(block);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("(" + id + " (");
        
        for (int i = ARGS_INDEX; i < TYPE_INDEX; i++) {
            if (i != ARGS_INDEX) { sb.append(" "); }
            sb.append(children.get(i).toString());
        }
        
        sb.append(") (");
    
        for (int i = TYPE_INDEX; i < BLOCK_INDEX; i++) {
            if (i != TYPE_INDEX) { sb.append(" "); }
            sb.append(children.get(i).toString());
        }

        sb.append(") (\n" + children.get(BLOCK_INDEX).toString() + "\n))");
        return sb.toString();
    }
}
