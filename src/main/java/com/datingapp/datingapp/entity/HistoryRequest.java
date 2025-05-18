package com.datingapp.datingapp.entity;

import lombok.Data;

@Data
public class HistoryRequest {
    private int limit;
    private int offset;

    public HistoryRequest() {}
    public HistoryRequest(int limit, int offset) {
        this.limit  = limit;
        this.offset = offset;
    }
}
