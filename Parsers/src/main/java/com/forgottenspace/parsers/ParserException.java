package com.forgottenspace.parsers;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = 4227035305250042729L;

	public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
