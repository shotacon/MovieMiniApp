package com.shotacon.movie.api.moviefan.exception;

public class BodyHandlerException extends Exception {

    private static final long serialVersionUID = 1L;

    public BodyHandlerException(String errorMsg) {
        super(errorMsg);
    }

    public BodyHandlerException(String errorMsg, Exception e) {
        super(errorMsg, e);
    }
}
