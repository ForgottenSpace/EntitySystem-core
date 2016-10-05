package com.forgottenspace.ai;

public class AiException extends RuntimeException {

	private static final long serialVersionUID = -6917165686137733837L;

	public AiException(String msg) {
        super(msg);
    }

    AiException(String msg, Throwable ex) {
        super(msg, ex);
    }

}
