package com.datingapp.datingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ChatAlreadyExistsException extends RuntimeException {
    public ChatAlreadyExistsException(String message) {
        super(message);
    }
}

