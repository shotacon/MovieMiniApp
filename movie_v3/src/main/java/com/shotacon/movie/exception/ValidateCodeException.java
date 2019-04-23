package com.shotacon.movie.exception;

public class ValidateCodeException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidateCodeException(String errorMsg) {
        super(errorMsg);
    }

    public ValidateCodeException(String errorMsg, Exception e) {
        super(errorMsg, e);
    }

}
