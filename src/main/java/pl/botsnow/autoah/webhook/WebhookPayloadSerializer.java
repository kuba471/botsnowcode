package pl.botsnow.autoah.webhook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class WebhookPayloadSerializer {
    public static JsonObject toJson(WebhookMessageModel model) {
        JsonObject root = new JsonObject();
        root.addProperty("content", model.content);

        JsonArray embeds = new JsonArray();
        for (WebhookEmbedModel embedModel : model.embeds) {
            JsonObject embed = new JsonObject();
            embed.addProperty("title", embedModel.title);
            embed.addProperty("description", embedModel.description);
            embed.addProperty("color", embedModel.color);
            embed.addProperty("timestamp", embedModel.timestamp);
            JsonArray fields = new JsonArray();
            for (WebhookField f : embedModel.fields) {
                JsonObject field = new JsonObject();
                field.addProperty("name", f.name);
                field.addProperty("value", f.value);
                field.addProperty("inline", f.inline);
                fields.add(field);
            }
            embed.add("fields", fields);
            embeds.add(embed);
        }
        root.add("embeds", embeds);
        return root;
    }
}
