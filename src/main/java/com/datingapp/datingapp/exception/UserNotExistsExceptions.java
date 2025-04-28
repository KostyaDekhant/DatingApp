package com.datingapp.datingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotExistsExceptions extends Throwable {
    private final int errorCode;

    public UserNotExistsExceptions(String s) {
        super(s);
        this.errorCode = 404;
    }

    public UserNotExistsExceptions(String s, int errorCode) {
        super(s);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
