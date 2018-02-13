package parser;

public class Use extends Node {
    
    private String file;

    public Use(String file) {
        this.file = file; 
    }

    public String toString() {
        return "use " + file;
    }
}
