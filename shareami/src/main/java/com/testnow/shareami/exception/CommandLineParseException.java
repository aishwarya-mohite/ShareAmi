package com.testnow.shareami.exception;

public class CommandLineParseException extends RuntimeException {
	private static final long serialVersionUID = -27013974181651638L;

	public CommandLineParseException() {
		super();
	}

	public CommandLineParseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommandLineParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandLineParseException(String message) {
		super(message);
	}

	public CommandLineParseException(Throwable cause) {
		super(cause);
	}
}

