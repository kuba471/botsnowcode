package pl.botsnow.autoah.util;

public class Numbers {
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
