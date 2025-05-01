package com.datingapp.datingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FormsNotFoundException extends RuntimeException {
    public FormsNotFoundException(String message) {
        super(message);
    }
}
