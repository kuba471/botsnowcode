package pl.botsnow.autoah.loop;

public class LoopClickSettings {
    public int clicksPerSecond = 200;
    public LoopClickPattern pattern = LoopClickPattern.RIGHT_LEFT;

    public int clicksPerTick() {
        return Math.max(1, clicksPerSecond / 20);
    }
}
