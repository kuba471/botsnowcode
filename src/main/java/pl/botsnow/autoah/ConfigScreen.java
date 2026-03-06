package pl.botsnow.autoah;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import pl.botsnow.autoah.loop.LoopExecutionStats;
import pl.botsnow.autoah.widget.HistoryRowFormatter;
import pl.botsnow.autoah.widget.UiHintMessages;
import pl.botsnow.autoah.widget.UiSectionTitles;
import pl.botsnow.autoah.widget.UiText;
import pl.botsnow.autoah.widget.WidgetMetrics;
import pl.botsnow.autoah.widget.WidgetTextPalette;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private int selectedIndex = 0;

    private TextFieldWidget ruleNameField;
    private TextFieldWidget itemIdField;
    private TextFieldWidget loreField;
    private TextFieldWidget enchantsField;
    private TextFieldWidget priceField;

    private TextFieldWidget webhookField;
    private TextFieldWidget reconnectAddressField;
    private TextFieldWidget reconnectDelayField;
    private TextFieldWidget loginPasswordField;
    private TextFieldWidget axeCpsField;

    private ButtonWidget ruleEnabledButton;
    private ButtonWidget autoReconnectButton;
    private ButtonWidget autoLoginButton;

    protected ConfigScreen(Screen parent) {
        super(Text.literal("BotSnow AutoAH Panel"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - 190;
        int top = 28;

        ruleNameField = addTextField(left + 12, top + 24, 170, "Nazwa reguły");
        itemIdField = addTextField(left + 12, top + 50, 170, "Item ID");
        loreField = addTextField(left + 12, top + 76, 170, "Lore contains");
        enchantsField = addTextField(left + 12, top + 102, 170, "Enchanty (,) ");
        priceField = addTextField(left + 12, top + 128, 170, "Max cena");

        webhookField = addTextField(left + 198, top + 24, 170, "Webhook URL");
        reconnectAddressField = addTextField(left + 198, top + 50, 170, "Adres reconnect");
        reconnectDelayField = addTextField(left + 198, top + 76, 170, "Delay (sekundy)");
        loginPasswordField = addTextField(left + 198, top + 102, 170, "Hasło /login");
        axeCpsField = addTextField(left + 198, top + 128, 170, "Axe CPS (10-400)");

        ruleEnabledButton = addDrawableChild(ButtonWidget.builder(Text.literal(""), b -> {
            RuleEntry entry = currentRule();
            entry.enabled = !entry.enabled;
            refreshButtons();
        }).dimensions(left + 12, top + 154, 170, 20).build());

        autoReconnectButton = addDrawableChild(ButtonWidget.builder(Text.literal(""), b -> {
            AutoAhConfig.autoReconnect = !AutoAhConfig.autoReconnect;
            refreshButtons();
        }).dimensions(left + 198, top + 154, 170, 20).build());

        autoLoginButton = addDrawableChild(ButtonWidget.builder(Text.literal(""), b -> {
            AutoAhConfig.autoLogin = !AutoAhConfig.autoLogin;
            refreshButtons();
        }).dimensions(left + 198, top + 178, 170, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("◀ Reguła"), b -> {
            saveAll();
            selectedIndex = Math.max(0, selectedIndex - 1);
            loadSelected();
        }).dimensions(left + 12, top + 182, 82, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reguła ▶"), b -> {
            saveAll();
            selectedIndex = Math.min(AutoAhConfig.RULES.size() - 1, selectedIndex + 1);
            loadSelected();
        }).dimensions(left + 100, top + 182, 82, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("+ Dodaj"), b -> {
            saveAll();
            RuleEntry entry = new RuleEntry();
            entry.name = "Reguła " + (AutoAhConfig.RULES.size() + 1);
            AutoAhConfig.RULES.add(entry);
            selectedIndex = AutoAhConfig.RULES.size() - 1;
            loadSelected();
        }).dimensions(left + 12, top + 206, 82, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("- Usuń"), b -> {
            if (AutoAhConfig.RULES.size() > 1) {
                AutoAhConfig.RULES.remove(selectedIndex);
                selectedIndex = Math.max(0, selectedIndex - 1);
                loadSelected();
            }
        }).dimensions(left + 100, top + 206, 82, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("ZAPISZ"), b -> saveAll()).dimensions(left + 198, top + 206, 82, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ZAMKNIJ"), b -> close()).dimensions(left + 286, top + 206, 82, 20).build());

        loadSelected();
        refreshButtons();
    }

    private TextFieldWidget addTextField(int x, int y, int width, String placeholder) {
        TextFieldWidget field = addDrawableChild(new TextFieldWidget(textRenderer, x, y, width, 20, Text.literal(placeholder)));
        field.setPlaceholder(Text.literal(placeholder));
        field.setMaxLength(300);
        return field;
    }

    private RuleEntry currentRule() {
        if (AutoAhConfig.RULES.isEmpty()) {
            AutoAhConfig.RULES.add(new RuleEntry());
        }
        if (selectedIndex < 0 || selectedIndex >= AutoAhConfig.RULES.size()) {
            selectedIndex = 0;
        }
        return AutoAhConfig.RULES.get(selectedIndex);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 2) {
            ItemStack hovered = getItemFromMainHand();
            if (!hovered.isEmpty()) {
                itemIdField.setText(Registries.ITEM.getId(hovered.getItem()).toString());
                if (ruleNameField.getText().isBlank()) {
                    ruleNameField.setText(hovered.getName().getString());
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private ItemStack getItemFromMainHand() {
        if (client == null || client.player == null) {
            return ItemStack.EMPTY;
        }
        List<Text> lines = new ArrayList<>(client.player.getMainHandStack().getTooltip(client.player, TooltipContext.BASIC));
        if (lines.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return client.player.getMainHandStack();
    }

    private void loadSelected() {
        RuleEntry entry = currentRule();
        ruleNameField.setText(entry.name);
        itemIdField.setText(entry.itemId);
        loreField.setText(entry.loreContains);
        enchantsField.setText(String.join(",", entry.requiredEnchants));
        priceField.setText(String.valueOf(entry.maxPrice));

        webhookField.setText(AutoAhConfig.webhookUrl);
        reconnectAddressField.setText(AutoAhConfig.reconnectAddress);
        reconnectDelayField.setText(String.valueOf(AutoAhConfig.reconnectDelaySeconds));
        loginPasswordField.setText(AutoAhConfig.loginPassword);
        axeCpsField.setText(String.valueOf(AutoAhConfig.axeClicksPerSecond));

        refreshButtons();
    }

    private void refreshButtons() {
        RuleEntry entry = currentRule();
        ruleEnabledButton.setMessage(Text.literal("Reguła: " + (entry.enabled ? "AKTYWNA" : "WYŁĄCZONA")));
        autoReconnectButton.setMessage(Text.literal("Reconnect: " + (AutoAhConfig.autoReconnect ? "ON" : "OFF")));
        autoLoginButton.setMessage(Text.literal("AutoLogin: " + (AutoAhConfig.autoLogin ? "ON" : "OFF")));
    }

    private void saveAll() {
        RuleEntry entry = currentRule();
        entry.name = ruleNameField.getText().trim().isBlank() ? "Reguła " + (selectedIndex + 1) : ruleNameField.getText().trim();
        entry.itemId = itemIdField.getText().trim();
        entry.loreContains = loreField.getText().trim();

        entry.requiredEnchants = new ArrayList<>();
        for (String part : enchantsField.getText().split(",")) {
            String s = part.trim();
            if (!s.isBlank()) {
                entry.requiredEnchants.add(s);
            }
        }

        try {
            entry.maxPrice = Long.parseLong(priceField.getText().trim());
        } catch (NumberFormatException e) {
            entry.maxPrice = 0;
        }

        AutoAhConfig.webhookUrl = webhookField.getText().trim();
        AutoAhConfig.reconnectAddress = reconnectAddressField.getText().trim().isBlank() ? "anarchia.gg" : reconnectAddressField.getText().trim();
        try {
            AutoAhConfig.reconnectDelaySeconds = Math.max(1, Integer.parseInt(reconnectDelayField.getText().trim()));
        } catch (NumberFormatException e) {
            AutoAhConfig.reconnectDelaySeconds = 3;
        }
        AutoAhConfig.loginPassword = loginPasswordField.getText().trim();
        try {
            AutoAhConfig.axeClicksPerSecond = Math.max(10, Math.min(400, Integer.parseInt(axeCpsField.getText().trim())));
        } catch (NumberFormatException e) {
            AutoAhConfig.axeClicksPerSecond = 200;
        }

        AutoAhConfig.save();
        refreshButtons();
    }

    @Override
    public void close() {
        saveAll();
        if (client != null) {
            client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int left = width / 2 - 190;
        int top = 28;
        int right = left + WidgetMetrics.PANEL_WIDTH;
        int bottom = top + WidgetMetrics.PANEL_HEIGHT;

        context.fill(left - 2, top - 2, right + 2, bottom + 2, 0xFF1A1A1A);
        context.fill(left, top, right, bottom, 0xCC101A2A);

        context.fill(left + 8, top + 20, left + 186, top + 230, 0x66356BFF);
        context.fill(left + 194, top + 20, right - 8, top + 230, 0x66FF4FA3);

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal(UiText.TITLE), width / 2, top + 6, WidgetTextPalette.WHITE);
        context.drawTextWithShadow(textRenderer, Text.literal("Reguła " + (selectedIndex + 1) + "/" + AutoAhConfig.RULES.size()), left + 12, top + 6, WidgetTextPalette.GOLD);
        context.drawTextWithShadow(textRenderer, Text.literal("AutoAH: " + (AutoAhConfig.enabled ? "§aON" : "§cOFF") + "  (toggle w keybinds)"), left + 194, top + 6, WidgetTextPalette.WHITE);

        LoopExecutionStats stats = AutoAhEngine.stats();
        context.drawTextWithShadow(textRenderer, Text.literal("§7Refresh clicks: §f" + stats.totalRefreshClicks), left + 194, top + 194, WidgetTextPalette.WHITE);
        context.drawTextWithShadow(textRenderer, Text.literal("§7Buy clicks: §f" + stats.totalBuyClicks), left + 194, top + 204, WidgetTextPalette.WHITE);

        context.drawTextWithShadow(textRenderer, Text.literal(UiSectionTitles.LAST_PURCHASES), left + 194, top + 218, WidgetTextPalette.WHITE);
        int rowY = top + 230;
        for (PurchaseRecord record : RuntimeState.getPurchases()) {
            context.drawTextWithShadow(textRenderer, Text.literal(HistoryRowFormatter.format(record)), left + 194, rowY, WidgetTextPalette.WHITE);
            rowY += 10;
        }

        context.drawTextWithShadow(textRenderer, Text.literal(UiHintMessages.MIDDLE_CLICK), left + 12, top + 232, WidgetTextPalette.WHITE);
    }
}
