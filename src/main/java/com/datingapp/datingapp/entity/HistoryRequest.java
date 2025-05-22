package com.datingapp.datingapp.entity;

import lombok.Data;

@Data
public class HistoryRequest {
    private int userId;
    private int limit;
    private int offset;

    public HistoryRequest() {}
    public HistoryRequest(int userId, int limit, int offset) {
        this.userId = userId;
        this.limit  = limit;
        this.offset = offset;
    }
}
