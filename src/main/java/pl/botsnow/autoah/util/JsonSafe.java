package pl.botsnow.autoah.util;

import com.google.gson.JsonObject;

public class JsonSafe {
    public static String getString(JsonObject root, String key, String fallback) {
        return root.has(key) ? root.get(key).getAsString() : fallback;
    }
}
