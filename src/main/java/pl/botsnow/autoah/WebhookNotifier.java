package pl.botsnow.autoah;

import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class WebhookNotifier {
    public static void send(String message) {
        if (AutoAhConfig.webhookUrl == null || AutoAhConfig.webhookUrl.isBlank()) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) URI.create(AutoAhConfig.webhookUrl).toURL().openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JsonObject payload = new JsonObject();
                payload.addProperty("content", message);
                byte[] body = payload.toString().getBytes();

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body);
                }
                connection.getResponseCode();
                connection.disconnect();
            } catch (Exception ignored) {
            }
        }, "botsnow-autoah-webhook");

        thread.setDaemon(true);
        thread.start();
    }
}
