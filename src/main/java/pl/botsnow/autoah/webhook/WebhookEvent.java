package pl.botsnow.autoah.webhook;

public class WebhookEvent {
    public WebhookEventType type;
    public String message;

    public static WebhookEvent status(String message) {
        WebhookEvent e = new WebhookEvent();
        e.type = WebhookEventType.STATUS;
        e.message = message;
        return e;
    }
}
