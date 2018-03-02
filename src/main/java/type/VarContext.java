package type;

/**
 * Symbol table mapping {@link ast.Var} id to {@link Type}.
 * 
 * @see Context
 */
public class VarContext extends Context<String, Type> {
	
	/**
	 * Default constructor initializes backing map.
	 */
	public VarContext() {
		super();
	}
	
	/**
	 * Private constructor for cloning.
	 * 
	 * @param context The context to clone
	 */
	private VarContext(VarContext context) {
		super(context);
	}

	/**
	 * Returns a copy of this context.
	 */
	public VarContext clone() {
		return new VarContext(this);
	}
}
