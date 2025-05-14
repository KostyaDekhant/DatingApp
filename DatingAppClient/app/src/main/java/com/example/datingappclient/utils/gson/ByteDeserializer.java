package com.example.datingappclient.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Base64;

public class ByteDeserializer implements JsonDeserializer<byte[]>, JsonSerializer<byte[]> {
    @Override
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("Expected base64 string for byte[]");
        }

        String base64 = json.getAsString();
        try {
            return Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid base64 input", e);
        }
    }

    @Override
    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return JsonNull.INSTANCE;
        String base64 = Base64.getEncoder().encodeToString(src);
        return new JsonPrimitive(base64);
    }
}
