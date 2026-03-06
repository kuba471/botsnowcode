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
import pl.botsnow.autoah.loop.LoopActionHistory;
import pl.botsnow.autoah.loop.LoopActionLogEntry;
import pl.botsnow.autoah.loop.LoopActionType;
import pl.botsnow.autoah.loop.LoopClickExecutor;
import pl.botsnow.autoah.loop.LoopClickSettings;
import pl.botsnow.autoah.loop.LoopExecutionStats;
import pl.botsnow.autoah.loop.LoopGuard;
import pl.botsnow.autoah.loop.LoopRateLimiter;
import pl.botsnow.autoah.loop.LoopTargetResolver;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AutoAhEngine {
    private static final LoopRateLimiter BUY_COOLDOWN = new LoopRateLimiter();
    private static final LoopClickSettings CLICK_SETTINGS = new LoopClickSettings();
    private static final LoopExecutionStats STATS = new LoopExecutionStats();
    private static final LoopActionHistory ACTION_HISTORY = new LoopActionHistory(20);

    public static void toggle(PlayerEntity player) {
        AutoAhConfig.enabled = !AutoAhConfig.enabled;
        AutoAhConfig.save();
        if (player != null) {
            player.sendMessage(Text.literal("[AutoAH] " + (AutoAhConfig.enabled ? "Włączono" : "Wyłączono")), true);
        }
    }

    public static void tick(MinecraftClient client) {
        if (!LoopGuard.canRun(client, AutoAhConfig.enabled)) {
            return;
        }

        if (!BUY_COOLDOWN.allow()) {
            return;
        }

        if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
            return;
        }

        CLICK_SETTINGS.clicksPerSecond = AutoAhConfig.axeClicksPerSecond;

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
            ACTION_HISTORY.add(LoopActionLogEntry.of(LoopActionType.BUY, i));
            STATS.addBuyClick();

            int limeDyeSlot = findLimeDyeSlot(handler);
            if (limeDyeSlot >= 0) {
                client.interactionManager.clickSlot(handler.syncId, limeDyeSlot, 0, SlotActionType.PICKUP, client.player);
                ACTION_HISTORY.add(LoopActionLogEntry.of(LoopActionType.CONFIRM, limeDyeSlot));
            }

            PurchaseRecord record = new PurchaseRecord();
            record.itemName = stack.getName().getString();
            record.itemId = Registries.ITEM.getId(stack.getItem()).toString();
            record.price = price;
            record.ruleName = rule.name;
            record.timeEpochMs = Instant.now().toEpochMilli();
            record.sellerName = PurchaseDetailsExtractor.extractSeller(stack);
            record.loreLines = PurchaseDetailsExtractor.extractLore(stack);
            record.sourceScreen = screen.getTitle().getString();
            RuntimeState.addPurchase(record);
            AutoAhConfig.save();

            WebhookNotifier.sendPurchase(record);
            BUY_COOLDOWN.setCooldownTicks(1);
            return;
        }
    }

    private static void spamRefreshAxe(MinecraftClient client, ScreenHandler handler) {
        int slot = LoopTargetResolver.findSlotWithItem(handler, "minecraft:iron_axe");
        if (slot < 0) {
            return;
        }

        int refreshClicks = LoopClickExecutor.clickBurst(client, handler, slot, CLICK_SETTINGS);
        STATS.addRefreshClicks(refreshClicks);
        ACTION_HISTORY.add(LoopActionLogEntry.of(LoopActionType.REFRESH, slot));
    }

    private static int findLimeDyeSlot(ScreenHandler handler) {
        return LoopTargetResolver.findSlotWithItem(handler, "minecraft:lime_dye");
    }

    public static LoopExecutionStats stats() {
        return STATS;
    }

    public static List<LoopActionLogEntry> actionLog() {
        return ACTION_HISTORY.snapshot();
    }
}
