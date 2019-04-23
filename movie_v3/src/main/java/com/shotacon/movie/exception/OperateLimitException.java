package com.shotacon.movie.exception;

public class OperateLimitException extends Exception {
    private static final long serialVersionUID = 1L;

    public OperateLimitException(int timeout) {
        super("操作过于频繁,请" + timeout + "秒后重试");
    }
}
