package com.staling.jwt.demo;

public class ProgramException extends RuntimeException {

    public ProgramException() {
        super();
    }

    public ProgramException(String message) {
        super(message);
    }

    public ProgramException(String message, Throwable e) {
        super(message, e);
    }
}
