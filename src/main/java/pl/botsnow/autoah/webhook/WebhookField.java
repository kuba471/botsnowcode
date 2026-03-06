package pl.botsnow.autoah.webhook;

public class WebhookField {
    public String name;
    public String value;
    public boolean inline;

    public WebhookField(String name, String value, boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }
}
