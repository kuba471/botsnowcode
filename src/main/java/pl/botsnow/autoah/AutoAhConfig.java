package pl.botsnow.autoah;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AutoAhConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("botsnow_autoah.json");

    public static final List<RuleEntry> RULES = new ArrayList<>();
    public static String webhookUrl = "";
    public static boolean enabled = false;

    public static void load() {
        RULES.clear();
        if (!Files.exists(CONFIG_PATH)) {
            RULES.add(new RuleEntry());
            save();
            return;
        }

        try {
            JsonObject root = GSON.fromJson(Files.readString(CONFIG_PATH), JsonObject.class);
            enabled = root.has("enabled") && root.get("enabled").getAsBoolean();
            webhookUrl = root.has("webhookUrl") ? root.get("webhookUrl").getAsString() : "";
            if (root.has("rules") && root.get("rules").isJsonArray()) {
                for (JsonElement e : root.getAsJsonArray("rules")) {
                    if (e.isJsonObject()) {
                        RULES.add(RuleEntry.fromJson(e.getAsJsonObject()));
                    }
                }
            }
            if (RULES.isEmpty()) {
                RULES.add(new RuleEntry());
            }
        } catch (Exception ex) {
            RULES.clear();
            RULES.add(new RuleEntry());
            save();
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();
        root.addProperty("enabled", enabled);
        root.addProperty("webhookUrl", webhookUrl);

        JsonArray arr = new JsonArray();
        for (RuleEntry rule : RULES) {
            arr.add(rule.toJson());
        }
        root.add("rules", arr);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
        } catch (IOException ignored) {
        }
    }
}
