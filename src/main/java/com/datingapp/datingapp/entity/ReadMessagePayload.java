package com.datingapp.datingapp.entity;

import lombok.Data;

import java.util.List;

@Data
public class ReadMessagePayload {
    int userId;
    int chatId;
    List<Integer> readMessageIds;
}
