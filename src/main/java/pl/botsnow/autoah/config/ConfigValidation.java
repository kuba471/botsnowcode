package pl.botsnow.autoah.config;

public class ConfigValidation {
    public static ConfigValidationResult validateAxeCps(int value) {
        ConfigValidationResult result = new ConfigValidationResult();
        result.valid = value >= ConfigRanges.AXE_CPS_MIN && value <= ConfigRanges.AXE_CPS_MAX;
        result.message = result.valid ? "ok" : "axeCps out of range";
        return result;
    }
}
