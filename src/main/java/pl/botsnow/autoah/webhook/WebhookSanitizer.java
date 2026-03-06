package pl.botsnow.autoah.webhook;

public class WebhookSanitizer {
    public static String safe(String value) {
        if (value == null || value.isBlank()) return "-";
        return value.replace("`", "'");
    }
}
