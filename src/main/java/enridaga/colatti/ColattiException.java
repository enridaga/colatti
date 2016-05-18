package enridaga.colatti;

public class ColattiException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColattiException(String msg) {
		super(msg);
	}

	public ColattiException(String msg, Throwable cause) {
		super(msg, cause);
	}
}