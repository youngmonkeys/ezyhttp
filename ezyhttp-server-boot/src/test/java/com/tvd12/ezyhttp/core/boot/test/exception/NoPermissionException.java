package com.tvd12.ezyhttp.core.boot.test.exception;

public class NoPermissionException extends RuntimeException {
    private static final long serialVersionUID = 7048184476214311405L;

    public NoPermissionException() {
        super("has no permission");
    }
}
