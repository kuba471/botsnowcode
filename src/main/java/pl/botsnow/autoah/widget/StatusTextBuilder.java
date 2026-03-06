package pl.botsnow.autoah.widget;

public class StatusTextBuilder {
    public static String autoAh(boolean enabled) {
        return "AutoAH: " + (enabled ? "§aON" : "§cOFF") + "  (toggle w keybinds)";
    }
}
