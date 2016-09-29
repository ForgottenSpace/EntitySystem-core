package com.ractoc.fs.ai;

public class AiException extends RuntimeException {

    public AiException(String msg) {
        super(msg);
    }

    AiException(String msg, Throwable ex) {
        super(msg, ex);
    }

}
