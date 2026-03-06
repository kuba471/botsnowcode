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

    public static boolean autoReconnect = true;
    public static String reconnectAddress = "anarchia.gg";
    public static int reconnectDelaySeconds = 3;

    public static boolean autoLogin = false;
    public static String loginPassword = "";

    public static void load() {
        RULES.clear();
        RuntimeState.setPurchases(new ArrayList<>());

        if (!Files.exists(CONFIG_PATH)) {
            RULES.add(new RuleEntry());
            save();
            return;
        }

        try {
            JsonObject root = GSON.fromJson(Files.readString(CONFIG_PATH), JsonObject.class);
            enabled = root.has("enabled") && root.get("enabled").getAsBoolean();
            webhookUrl = root.has("webhookUrl") ? root.get("webhookUrl").getAsString() : "";
            autoReconnect = !root.has("autoReconnect") || root.get("autoReconnect").getAsBoolean();
            reconnectAddress = root.has("reconnectAddress") ? root.get("reconnectAddress").getAsString() : "anarchia.gg";
            reconnectDelaySeconds = root.has("reconnectDelaySeconds") ? root.get("reconnectDelaySeconds").getAsInt() : 3;
            autoLogin = root.has("autoLogin") && root.get("autoLogin").getAsBoolean();
            loginPassword = root.has("loginPassword") ? root.get("loginPassword").getAsString() : "";

            if (root.has("rules") && root.get("rules").isJsonArray()) {
                for (JsonElement e : root.getAsJsonArray("rules")) {
                    if (e.isJsonObject()) {
                        RULES.add(RuleEntry.fromJson(e.getAsJsonObject()));
                    }
                }
            }

            if (root.has("lastPurchases") && root.get("lastPurchases").isJsonArray()) {
                List<PurchaseRecord> records = new ArrayList<>();
                for (JsonElement e : root.getAsJsonArray("lastPurchases")) {
                    if (e.isJsonObject()) {
                        records.add(PurchaseRecord.fromJson(e.getAsJsonObject()));
                    }
                }
                RuntimeState.setPurchases(records);
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
        root.addProperty("autoReconnect", autoReconnect);
        root.addProperty("reconnectAddress", reconnectAddress);
        root.addProperty("reconnectDelaySeconds", reconnectDelaySeconds);
        root.addProperty("autoLogin", autoLogin);
        root.addProperty("loginPassword", loginPassword);

        JsonArray arr = new JsonArray();
        for (RuleEntry rule : RULES) {
            arr.add(rule.toJson());
        }
        root.add("rules", arr);

        JsonArray purchases = new JsonArray();
        for (PurchaseRecord record : RuntimeState.getPurchases()) {
            purchases.add(record.toJson());
        }
        root.add("lastPurchases", purchases);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
        } catch (IOException ignored) {
        }
    }
}
