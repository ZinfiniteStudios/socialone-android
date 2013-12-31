package com.socialone.android.appnet.adnlib.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.socialone.android.appnet.adnlib.data.Count;

import java.lang.reflect.Type;

class CountDeserializer implements JsonDeserializer<Count> {
    @Override
    public Count deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return new Count(json.getAsInt());
    }
}