package xic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import static xic.Opt.*;

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
    private boolean help = false;

    /**
     * Report supported optimizations.
     */
    private boolean report = false;

    //
    // Directory Options
    //

	/**
	 * Directory to search for source files.
	 */
	private String source = "";

	/**
	 * Directory to generate diagnostic files.
	 */
	private String sink = "";

	/**
	 * Directory to generate assembly files.
	 */
	private String asm = "";

	/**
	 * Directory to search for library files.
	 */
	private String lib = "";

    //
    // Diagnostic Options
    //
    
    /**
     * Lexing phase of compilation with optional diagnostics.
     */
    private LexPhase lex = new LexPhase();

    /**
     * Parsing phase of compilation with optional diagnostics.
     */
    private ParsePhase parse = new ParsePhase();

    /**
     * Typechecking phase of compilation with optional diagnostics.
     */
    private TypePhase type = new TypePhase();

    /**
     * IR emitting phase of compilation with optional diagnostics.
     */
    private EmitPhase emit = new EmitPhase();

    /**
     * (Optional) IR interpreting phase of compilation.
     */
    private List<InterpretPhase> interpet = new ArrayList<>();

    /**
     * (Optional) IR optimizing phase of compilation with optional diagnostics.
     */
    private List<OptimizePhase> optimize = new ArrayList<OptimizePhase>();

    //
    // Compilation Options
    //
    
    /**
     * Enable compiler optimizations.
     */
    private Set<Opt> opt = new HashSet<>(Arrays.asList(CF, REG, CSE, MC, CP));

	/**
	 * Target the given OS.
	 */
	private String os = "linux";

	/**
	 * Compile the given files.
	 */
	private List<String> files = new ArrayList<>();

    public void setHelp() { help = true; }

    public void setReport() { report = true; }

    public void setSource(String source) { this.source = source; }

    public void setSink(String sink) { this.sink = sink; }

    public void setAsm(String asm) { this.asm = asm; }

    public void setLib(String lib) { this.lib = lib; }

    public void setLex() { this.lex.setOutput(); }

    public void setParse() { this.parse.setOutput(); }

    public void setType() { this.type.setOutput(); }

    public void setEmit() { this.emit.setOutput(); }
}
