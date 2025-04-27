package com.datingapp.datingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsExceptions extends RuntimeException {
    public UserAlreadyExistsExceptions(String s) {
        super(s);
    }
}
