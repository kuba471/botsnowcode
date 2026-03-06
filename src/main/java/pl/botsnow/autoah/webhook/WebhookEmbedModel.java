package pl.botsnow.autoah.webhook;

import java.util.ArrayList;
import java.util.List;

public class WebhookEmbedModel {
    public String title;
    public String description;
    public int color;
    public String timestamp;
    public final List<WebhookField> fields = new ArrayList<>();
}
