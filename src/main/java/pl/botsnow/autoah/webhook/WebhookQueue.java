package pl.botsnow.autoah.webhook;

import java.util.ArrayDeque;
import java.util.Deque;

public class WebhookQueue {
    private final Deque<WebhookEvent> queue = new ArrayDeque<>();

    public void push(WebhookEvent event) {
        queue.addLast(event);
    }

    public WebhookEvent poll() {
        return queue.pollFirst();
    }
}
