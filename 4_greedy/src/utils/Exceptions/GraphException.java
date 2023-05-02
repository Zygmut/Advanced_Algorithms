package utils.Exceptions;

public class GraphException extends RuntimeException {

	public GraphException(String message) {
		super(message);
	}

	public GraphException(String message, Throwable cause) {
		super(message, cause);
	}

	public GraphException(Throwable cause) {
		super(cause);
	}

	public GraphException() {
		super();
	}
}
