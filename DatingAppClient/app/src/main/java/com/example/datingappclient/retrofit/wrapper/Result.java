package com.example.datingappclient.retrofit.wrapper;

public class Result<T> {
    public enum Status {
        SUCCESS,
        ERROR,
        EMPTY
    }

    public final Status status;
    public final T data;
    public final String error;

    private Result(Status status, T data, String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.SUCCESS, data, null);
    }

    public static <T> Result<T> empty() {
        return new Result<>(Status.EMPTY, null, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(Status.ERROR, null, message);
    }
}
