package pl.botsnow.autoah.webhook;

public class WebhookDiagnostics {
    public long sent;
    public long failed;

    public void markSent() { sent++; }
    public void markFailed() { failed++; }
}
