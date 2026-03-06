package pl.botsnow.autoah;

import com.google.gson.JsonObject;

public class PurchaseRecord {
    public String itemName;
    public long price;
    public String ruleName;
    public long timeEpochMs;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("itemName", itemName);
        object.addProperty("price", price);
        object.addProperty("ruleName", ruleName);
        object.addProperty("timeEpochMs", timeEpochMs);
        return object;
    }

    public static PurchaseRecord fromJson(JsonObject object) {
        PurchaseRecord record = new PurchaseRecord();
        record.itemName = object.has("itemName") ? object.get("itemName").getAsString() : "?";
        record.price = object.has("price") ? object.get("price").getAsLong() : 0;
        record.ruleName = object.has("ruleName") ? object.get("ruleName").getAsString() : "?";
        record.timeEpochMs = object.has("timeEpochMs") ? object.get("timeEpochMs").getAsLong() : 0L;
        return record;
    }
}
