package pl.botsnow.autoah.config;

public class ConfigValidationResult {
    public boolean valid;
    public String message;

    public static ConfigValidationResult ok() {
        ConfigValidationResult r = new ConfigValidationResult();
        r.valid = true;
        r.message = "ok";
        return r;
    }
}
