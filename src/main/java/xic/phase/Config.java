package xic.phase;

/**
 * Represents the target for a single Xic pipeline pass.
 */
public class Config {

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

    /**
     * File to compile.
     */
    public String unit = "";
}
