package com.shotacon.movie.exception;

public class MapRequestException extends Exception {

    private static final long serialVersionUID = 1L;

    public MapRequestException(String errorMsg) {
        super(errorMsg);
    }

    public MapRequestException(String errorMsg, Exception e) {
        super(errorMsg, e);
    }

}
