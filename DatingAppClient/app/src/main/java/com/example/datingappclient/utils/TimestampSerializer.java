package com.example.datingappclient.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;

public class TimestampSerializer implements JsonSerializer<Timestamp> {
    @Override
    public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
        if (src != null) {
            // Возвращаем значение в миллисекундах как JsonPrimitive
            return new JsonPrimitive(src.getTime());
        }
        // Если src равен null, возвращаем null
        return null;
    }
}
