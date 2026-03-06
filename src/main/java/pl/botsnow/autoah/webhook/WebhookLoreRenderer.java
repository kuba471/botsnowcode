package pl.botsnow.autoah.webhook;

import java.util.List;

public class WebhookLoreRenderer {
    public static String toInline(List<String> lore) {
        if (lore == null || lore.isEmpty()) return "*(brak / nie wykryto)*";
        return String.join(" | ", lore);
    }
}
