package parser;

public class Use extends Node {
    
    private String file;

    public Use(String file) {
        this.file = file; 
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
