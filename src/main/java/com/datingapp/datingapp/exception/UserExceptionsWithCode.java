package com.datingapp.datingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExceptionsWithCode extends Throwable {
    private final int errorCode;

    public UserExceptionsWithCode(String s, int errorCode) {
        super(s);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
