package pl.botsnow.autoah.loop;

public class LoopRateLimiter {
    private int cooldown;

    public boolean allow() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        return true;
    }

    public void setCooldownTicks(int ticks) {
        this.cooldown = Math.max(0, ticks);
    }
}
