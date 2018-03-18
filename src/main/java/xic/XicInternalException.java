package xic;

/**
 * Represents the set of all possible compiler exceptions.
 */
@SuppressWarnings("serial")
public class XicInternalException extends RuntimeException {
	
	/**
	 * The written description of the exception.
	 */
	private String description;
	
	/**
	 * Creates an exception with no location information. Only used
	 * for non-compilation related exceptions (e.g. IOExceptions)
	 */
	public XicInternalException(String description) {
		this.description = description;
	}

	/**
	 * Deprecated in favor of more descriptive alternatives, as according
	 * to the specification.
	 */
    @Override
	public String toString() { return description; }

	/**
	 * Factory method for an internal error.
	 */
	public static XicInternalException internal(String msg) {
		return new XicInternalException("Internal error: " + msg);
	}
}