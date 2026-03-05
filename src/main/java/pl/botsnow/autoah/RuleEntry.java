package pl.botsnow.autoah;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class RuleEntry {
    public String itemId = "minecraft:stone";
    public String loreContains = "";
    public List<String> requiredEnchants = new ArrayList<>();
    public long maxPrice = 0;

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("itemId", itemId);
        obj.addProperty("loreContains", loreContains);
        obj.addProperty("maxPrice", maxPrice);
        JsonArray arr = new JsonArray();
        for (String s : requiredEnchants) {
            arr.add(s);
        }
        obj.add("requiredEnchants", arr);
        return obj;
    }

    public static RuleEntry fromJson(JsonObject obj) {
        RuleEntry entry = new RuleEntry();
        entry.itemId = obj.has("itemId") ? obj.get("itemId").getAsString() : entry.itemId;
        entry.loreContains = obj.has("loreContains") ? obj.get("loreContains").getAsString() : "";
        entry.maxPrice = obj.has("maxPrice") ? obj.get("maxPrice").getAsLong() : 0;

        if (obj.has("requiredEnchants") && obj.get("requiredEnchants").isJsonArray()) {
            entry.requiredEnchants.clear();
            for (JsonElement element : obj.getAsJsonArray("requiredEnchants")) {
                entry.requiredEnchants.add(element.getAsString());
            }
        }
        return entry;
    }
}
