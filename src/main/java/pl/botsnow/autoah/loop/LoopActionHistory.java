package pl.botsnow.autoah.loop;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class LoopActionHistory {
    private final Deque<LoopActionLogEntry> entries = new ArrayDeque<>();
    private final int max;

    public LoopActionHistory(int max) {
        this.max = Math.max(1, max);
    }

    public void add(LoopActionLogEntry entry) {
        entries.addFirst(entry);
        while (entries.size() > max) entries.removeLast();
    }

    public List<LoopActionLogEntry> snapshot() {
        return List.copyOf(entries);
    }
}
