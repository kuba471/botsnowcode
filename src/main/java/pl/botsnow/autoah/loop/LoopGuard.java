package pl.botsnow.autoah.loop;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class LoopGuard {
    public static boolean canRun(MinecraftClient client, boolean enabled) {
        return enabled && client.player != null && client.interactionManager != null && (client.currentScreen instanceof HandledScreen<?>);
    }
}
