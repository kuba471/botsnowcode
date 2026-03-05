package pl.botsnow.autoah;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AutoAhEngine {
    private static int clickCooldown = 0;

    public static void toggle(PlayerEntity player) {
        AutoAhConfig.enabled = !AutoAhConfig.enabled;
        AutoAhConfig.save();
        if (player != null) {
            player.sendMessage(Text.literal("[AutoAH] " + (AutoAhConfig.enabled ? "Włączono" : "Wyłączono")), true);
        }
    }

    public static void tick(MinecraftClient client) {
        if (clickCooldown > 0) {
            clickCooldown--;
        }

        if (!AutoAhConfig.enabled || client.player == null || client.interactionManager == null) {
            return;
        }

        if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
            return;
        }

        if (clickCooldown > 0) {
            return;
        }

        ScreenHandler handler = screen.getScreenHandler();
        spamRefreshAxe(client, handler);

        for (int i = 0; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) {
                continue;
            }

            Optional<RuleEntry> matched = RuleMatcher.findMatch(stack);
            if (matched.isEmpty()) {
                continue;
            }

            RuleEntry rule = matched.get();
            long price = PriceParser.parsePrice(stack);
            if (price <= 0 || price > rule.maxPrice) {
                continue;
            }

            client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP, client.player);

            int limeDyeSlot = findLimeDyeSlot(handler.slots);
            if (limeDyeSlot >= 0) {
                client.interactionManager.clickSlot(handler.syncId, limeDyeSlot, 0, SlotActionType.PICKUP, client.player);
            }

            PurchaseRecord record = new PurchaseRecord();
            record.itemName = stack.getName().getString();
            record.price = price;
            record.ruleName = rule.name;
            record.timeEpochMs = Instant.now().toEpochMilli();
            RuntimeState.addPurchase(record);
            AutoAhConfig.save();

            WebhookNotifier.send("✅ Kupiono: " + record.itemName + " | cena: " + record.price + " | reguła: " + record.ruleName);
            clickCooldown = 1;
            return;
        }
    }

    private static void spamRefreshAxe(MinecraftClient client, ScreenHandler handler) {
        for (int i = 0; i < handler.slots.size(); i++) {
            ItemStack stack = handler.slots.get(i).getStack();
            if (stack.isEmpty()) {
                continue;
            }
            Identifier id = Registries.ITEM.getId(stack.getItem());
            if ("minecraft:iron_axe".equals(id.toString())) {
                client.interactionManager.clickSlot(handler.syncId, i, 1, SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP, client.player);
                return;
            }
        }
    }

    private static int findLimeDyeSlot(List<Slot> slots) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i).getStack();
            if (stack.isEmpty()) {
                continue;
            }
            Identifier id = Registries.ITEM.getId(stack.getItem());
            if ("minecraft:lime_dye".equals(id.toString())) {
                return i;
            }
        }
        return -1;
    }
}
