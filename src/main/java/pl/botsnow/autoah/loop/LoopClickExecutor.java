package pl.botsnow.autoah.loop;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class LoopClickExecutor {
    public static int clickBurst(MinecraftClient client, ScreenHandler handler, int slot, LoopClickSettings settings) {
        int clicks = settings.clicksPerTick();
        for (int i = 0; i < clicks; i++) {
            int button = buttonForIndex(i, settings.pattern);
            client.interactionManager.clickSlot(handler.syncId, slot, button, SlotActionType.PICKUP, client.player);
        }
        return clicks;
    }

    private static int buttonForIndex(int i, LoopClickPattern pattern) {
        return switch (pattern) {
            case RIGHT_LEFT -> (i % 2 == 0) ? 1 : 0;
            case LEFT_RIGHT -> (i % 2 == 0) ? 0 : 1;
            case RIGHT_ONLY -> 1;
            case LEFT_ONLY -> 0;
        };
    }
}
