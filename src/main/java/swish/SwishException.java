package swish;

public class SwishException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    public SwishException(String message) {
        super(message);
    }

    public SwishException(String message, Throwable cause) {
        super(message, cause);
    }
}
