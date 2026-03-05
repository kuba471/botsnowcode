package pl.botsnow.autoah;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AutoAhMod implements ClientModInitializer {
    public static final String MOD_ID = "botsnow_autoah";

    private static KeyBinding openGuiKey;
    private static KeyBinding toggleAutoAhKey;

    @Override
    public void onInitializeClient() {
        AutoAhConfig.load();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.botsnow_autoah.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.botsnow_autoah"
        ));

        toggleAutoAhKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.botsnow_autoah.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                "category.botsnow_autoah"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(MinecraftClient client) {
        while (openGuiKey.wasPressed()) {
            client.setScreen(new ConfigScreen(client.currentScreen));
        }

        while (toggleAutoAhKey.wasPressed()) {
            AutoAhEngine.toggle(client.player);
        }

        AutoAhEngine.tick(client);
    }
}
