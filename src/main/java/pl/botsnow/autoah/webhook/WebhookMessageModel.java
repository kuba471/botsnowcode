package pl.botsnow.autoah.webhook;

import java.util.ArrayList;
import java.util.List;

public class WebhookMessageModel {
    public String content;
    public final List<WebhookEmbedModel> embeds = new ArrayList<>();
}
