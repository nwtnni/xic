package xic.phase;

/**
 * Represents the target for a single Xic pipeline pass.
 */
public class Config {

	/**
	 * Directory to search for source files.
	 */
	public final String source;

	/**
	 * Directory to generate diagnostic files.
	 */
	public final String sink;

	/**
	 * Directory to generate assembly files.
	 */
	public final String asm;

	/**
	 * Directory to search for library files.
	 */
	public final String lib;

    /**
     * File to compile.
     */
    public String unit;

    public Config(String source, String sink, String asm, String lib)  {
        this.source = source;
        this.sink = sink;
        this.asm = asm;
        this.lib = lib;
    }

    public void setUnit(String unit) { this.unit = unit; }
}
