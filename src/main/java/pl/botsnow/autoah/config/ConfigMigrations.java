package pl.botsnow.autoah.config;

public class ConfigMigrations {
    public static int migrateAxeCps(Integer current) {
        if (current == null) return ConfigDefaults.AXE_CPS;
        return ConfigClamp.axeCps(current);
    }
}
