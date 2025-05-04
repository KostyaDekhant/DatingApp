package com.datingapp.datingapp.exception;

public class ResourceNotFoundException extends Throwable {
    public ResourceNotFoundException(String user, int userId) {
        super("User with id " + userId + " not found");
    }
}
