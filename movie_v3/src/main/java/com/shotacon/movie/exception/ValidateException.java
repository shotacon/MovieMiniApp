package com.shotacon.movie.exception;

public class ValidateException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidateException(String errorMsg) {
        super(errorMsg);
    }

    public ValidateException(String errorMsg, Exception e) {
        super(errorMsg, e);
    }

}
