package pl.botsnow.autoah;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoAhEngine {
    private static final Pattern PRICE_PATTERN = Pattern.compile("(?:^|\\D)(\\d[\\d\\s_.,]*)$");
    private static int clickCooldown = 0;

    public static void toggle(PlayerEntity player) {
        AutoAhConfig.enabled = !AutoAhConfig.enabled;
        AutoAhConfig.save();
        if (player != null) {
            player.sendMessage(Text.literal("[AutoAH] " + (AutoAhConfig.enabled ? "Włączono" : "Wyłączono")), true);
        }
    }

    public static void tick(MinecraftClient client) {
        if (!AutoAhConfig.enabled || client.player == null || client.interactionManager == null) {
            return;
        }

        if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
            return;
        }

        ScreenHandler handler = screen.getScreenHandler();
        if (clickCooldown > 0) {
            clickCooldown--;
            return;
        }

        spamRefreshAxe(client, handler);

        for (int i = 0; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            Optional<RuleEntry> matched = matchRule(stack);
            if (matched.isEmpty()) continue;

            long price = readPrice(stack);
            if (price <= 0 || price > matched.get().maxPrice) continue;

            client.interactionManager.clickSlot(handler.syncId, i, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, client.player);

            int limeDyeSlot = findLimeDyeSlot(handler.slots);
            if (limeDyeSlot >= 0) {
                client.interactionManager.clickSlot(handler.syncId, limeDyeSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, client.player);
            }

            WebhookNotifier.send("✅ Kupiono: " + stack.getName().getString() + " za " + price);
            clickCooldown = 1;
            return;
        }
    }

    private static void spamRefreshAxe(MinecraftClient client, ScreenHandler handler) {
        for (int i = 0; i < handler.slots.size(); i++) {
            ItemStack stack = handler.slots.get(i).getStack();
            if (stack.isEmpty()) continue;
            Identifier id = Registries.ITEM.getId(stack.getItem());
            if ("minecraft:iron_axe".equals(id.toString())) {
                client.interactionManager.clickSlot(handler.syncId, i, 1, net.minecraft.screen.slot.SlotActionType.PICKUP, client.player);
                client.interactionManager.clickSlot(handler.syncId, i, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, client.player);
                return;
            }
        }
    }

    private static Optional<RuleEntry> matchRule(ItemStack stack) {
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        List<Text> loreLines = stack.getTooltip(null, net.minecraft.client.item.TooltipContext.BASIC);
        String tooltip = loreLines.stream().map(Text::getString).reduce("", (a, b) -> a + "\n" + b).toLowerCase(Locale.ROOT);

        for (RuleEntry rule : AutoAhConfig.RULES) {
            if (!rule.itemId.equalsIgnoreCase(itemId)) continue;
            if (!rule.loreContains.isBlank() && !tooltip.contains(rule.loreContains.toLowerCase(Locale.ROOT))) continue;

            boolean enchantsOk = true;
            for (String enchantNeedle : rule.requiredEnchants) {
                if (!tooltip.contains(enchantNeedle.toLowerCase(Locale.ROOT))) {
                    enchantsOk = false;
                    break;
                }
            }
            if (enchantsOk) return Optional.of(rule);
        }
        return Optional.empty();
    }

    private static long readPrice(ItemStack stack) {
        List<Text> loreLines = stack.getTooltip(null, net.minecraft.client.item.TooltipContext.BASIC);
        long found = -1;

        for (Text line : loreLines) {
            String raw = line.getString().replace(" ", "");
            Matcher matcher = PRICE_PATTERN.matcher(raw);
            if (matcher.find()) {
                String digits = matcher.group(1).replaceAll("[^0-9]", "");
                if (!digits.isBlank()) {
                    try {
                        found = Long.parseLong(digits);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return found;
    }

    private static int findLimeDyeSlot(List<Slot> slots) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i).getStack();
            if (stack.isEmpty()) continue;
            Identifier id = Registries.ITEM.getId(stack.getItem());
            if ("minecraft:lime_dye".equals(id.toString())) {
                return i;
            }
        }
        return -1;
    }
}
