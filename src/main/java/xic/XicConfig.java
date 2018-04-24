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
     * Lexing phase of compilation.
     */
    public LexPhase lex = new LexPhase(false);

    /**
     * Parsing phase of compilation.
     */
    public ParsePhase parse = new ParsePhase(false);

    /**
     * Typechecking phase of compilation.
     */
    public TypePhase type = new TypePhase(false);

    /**
     * IR emitting phase of compilation.
     */
    public EmitPhase emit = new EmitPhase(false);

    /**
     * (Optional) IR interpreting phase of compilation.
     */
    public List<InterpretPhase> interpet = new ArrayList<>();

    public List<OptimizePhase> optimize = new ArrayList<>();

    //
    // Compilation Options
    //

	/**
	 * Target the given OS.
	 */
	public String os = "linux";

	/**
	 * Compile the given files.
	 */
	public List<String> files = new ArrayList<>();
}
