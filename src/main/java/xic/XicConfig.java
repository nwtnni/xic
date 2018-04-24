package xic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents possible Xic command-line options.
 */
public class XicConfig {

    //
    // Compiler Information
    // 

    /**
     * Print help information.
     */
    public boolean help = false;

    /**
     * Report supported optimizations.
     */
    public boolean report = false;

    //
    // Directory Options
    //

	/**
	 * Directory to search for source files.
	 */
	public String source = "";
	
	/**
	 * Directory to generate diagnostic files.
	 */
	public String sink = "";
	
	/**
	 * Directory to generate assembly files.
	 */
	public String asm = "";
	
	/**
	 * Directory to search for library files.
	 */
	public String lib = "";

    //
    // Diagnostic Options
    //
	
	/**
	 * Generate lexical diagnostic files.
	 */
	public boolean lex = false;
	
	/**
	 * Generate syntactic diagnostic files.
	 */
	public boolean parse = false;
	
	/**
	 * Generate semantic diagnostic files.
	 */
	public boolean type = false;
	
	/**
	 * Generate IR diagnostic files.
	 */
	public boolean emit = false;
	
	/**
	 * Simulate the generated IR.
	 */
	public boolean interpret = false;
	
	/**
	 * Constant fold the generated IR.
	 */
	public boolean optimize = true;
	
	/**
	 * Target the given OS.
	 */
	public String os = "linux";
	
	/**
	 * Compile the given files.
	 */
	public List<String> files = new ArrayList<>();
}
