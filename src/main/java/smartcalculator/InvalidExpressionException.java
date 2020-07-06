package smartcalculator;

class InvalidExpressionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidExpressionException() {
		super();
	}

	public InvalidExpressionException(String message) {
		super(message);
	}

	public InvalidExpressionException(Throwable cause) {
		super(cause);
	}

	public InvalidExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidExpressionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
