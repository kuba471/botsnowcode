package pl.botsnow.autoah;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class RuleEntry {
    public String name = "Nowa reguła";
    public String itemId = "minecraft:stone";
    public String loreContains = "";
    public List<String> requiredEnchants = new ArrayList<>();
    public long maxPrice = 0;
    public boolean enabled = true;

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("itemId", itemId);
        obj.addProperty("loreContains", loreContains);
        obj.addProperty("maxPrice", maxPrice);
        obj.addProperty("enabled", enabled);

        JsonArray arr = new JsonArray();
        for (String s : requiredEnchants) {
            arr.add(s);
        }
        obj.add("requiredEnchants", arr);
        return obj;
    }

    public static RuleEntry fromJson(JsonObject obj) {
        RuleEntry entry = new RuleEntry();
        entry.name = obj.has("name") ? obj.get("name").getAsString() : entry.name;
        entry.itemId = obj.has("itemId") ? obj.get("itemId").getAsString() : entry.itemId;
        entry.loreContains = obj.has("loreContains") ? obj.get("loreContains").getAsString() : "";
        entry.maxPrice = obj.has("maxPrice") ? obj.get("maxPrice").getAsLong() : 0;
        entry.enabled = !obj.has("enabled") || obj.get("enabled").getAsBoolean();

        if (obj.has("requiredEnchants") && obj.get("requiredEnchants").isJsonArray()) {
            entry.requiredEnchants.clear();
            for (JsonElement element : obj.getAsJsonArray("requiredEnchants")) {
                entry.requiredEnchants.add(element.getAsString());
            }
        }

        return entry;
    }
}
