package pl.botsnow.autoah.loop;

public class LoopActionLogEntry {
    public LoopActionType actionType;
    public int slot;
    public long timestamp;

    public static LoopActionLogEntry of(LoopActionType type, int slot) {
        LoopActionLogEntry e = new LoopActionLogEntry();
        e.actionType = type;
        e.slot = slot;
        e.timestamp = System.currentTimeMillis();
        return e;
    }
}
