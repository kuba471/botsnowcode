package pl.botsnow.autoah.loop;

public class LoopExecutionStats {
    public long totalRefreshClicks;
    public long totalBuyClicks;

    public void addRefreshClicks(int count) {
        totalRefreshClicks += Math.max(0, count);
    }

    public void addBuyClick() {
        totalBuyClicks++;
    }
}
