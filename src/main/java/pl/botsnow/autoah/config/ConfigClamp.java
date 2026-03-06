package pl.botsnow.autoah.config;

public class ConfigClamp {
    public static int axeCps(int value) {
        return Math.max(ConfigRanges.AXE_CPS_MIN, Math.min(ConfigRanges.AXE_CPS_MAX, value));
    }
}
