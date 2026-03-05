package pl.botsnow.autoah;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private int selectedIndex = 0;

    private TextFieldWidget itemIdField;
    private TextFieldWidget loreField;
    private TextFieldWidget enchantsField;
    private TextFieldWidget priceField;
    private TextFieldWidget webhookField;

    protected ConfigScreen(Screen parent) {
        super(Text.literal("BotSnow AutoAH"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = height / 2 - 90;

        itemIdField = addDrawableChild(new TextFieldWidget(textRenderer, centerX - 150, y, 300, 20, Text.literal("Item ID")));
        itemIdField.setMaxLength(128);
        y += 24;

        loreField = addDrawableChild(new TextFieldWidget(textRenderer, centerX - 150, y, 300, 20, Text.literal("Lore contains")));
        loreField.setMaxLength(200);
        y += 24;

        enchantsField = addDrawableChild(new TextFieldWidget(textRenderer, centerX - 150, y, 300, 20, Text.literal("Enchanty (po przecinku)")));
        enchantsField.setMaxLength(200);
        y += 24;

        priceField = addDrawableChild(new TextFieldWidget(textRenderer, centerX - 150, y, 300, 20, Text.literal("Max cena")));
        priceField.setMaxLength(32);
        y += 24;

        webhookField = addDrawableChild(new TextFieldWidget(textRenderer, centerX - 150, y, 300, 20, Text.literal("Webhook URL")));
        webhookField.setText(AutoAhConfig.webhookUrl);
        webhookField.setMaxLength(300);
        y += 28;

        addDrawableChild(ButtonWidget.builder(Text.literal("Poprzedni"), b -> {
            selectedIndex = Math.max(0, selectedIndex - 1);
            loadSelected();
        }).dimensions(centerX - 150, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Następny"), b -> {
            selectedIndex = Math.min(AutoAhConfig.RULES.size() - 1, selectedIndex + 1);
            loadSelected();
        }).dimensions(centerX - 50, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Nowa reguła"), b -> {
            AutoAhConfig.RULES.add(new RuleEntry());
            selectedIndex = AutoAhConfig.RULES.size() - 1;
            loadSelected();
        }).dimensions(centerX + 50, y, 95, 20).build());

        y += 24;

        addDrawableChild(ButtonWidget.builder(Text.literal("Usuń regułę"), b -> {
            if (AutoAhConfig.RULES.size() > 1) {
                AutoAhConfig.RULES.remove(selectedIndex);
                selectedIndex = Math.max(0, selectedIndex - 1);
                loadSelected();
            }
        }).dimensions(centerX - 150, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Zapisz"), b -> saveAll()).dimensions(centerX - 50, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Wróć"), b -> close()).dimensions(centerX + 50, y, 95, 20).build());

        loadSelected();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 2 && client != null && client.currentScreen != null && client.currentScreen == this) {
            ItemStack hovered = getItemFromCrosshair();
            if (!hovered.isEmpty()) {
                itemIdField.setText(Registries.ITEM.getId(hovered.getItem()).toString());
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private ItemStack getItemFromCrosshair() {
        if (client == null || client.player == null) return ItemStack.EMPTY;
        List<Text> lines = new ArrayList<>(client.player.getMainHandStack().getTooltip(client.player, TooltipContext.BASIC));
        if (lines.isEmpty()) return ItemStack.EMPTY;
        return client.player.getMainHandStack();
    }

    private void loadSelected() {
        if (AutoAhConfig.RULES.isEmpty()) {
            AutoAhConfig.RULES.add(new RuleEntry());
        }

        RuleEntry entry = AutoAhConfig.RULES.get(selectedIndex);
        itemIdField.setText(entry.itemId);
        loreField.setText(entry.loreContains);
        enchantsField.setText(String.join(",", entry.requiredEnchants));
        priceField.setText(String.valueOf(entry.maxPrice));
    }

    private void saveAll() {
        RuleEntry entry = AutoAhConfig.RULES.get(selectedIndex);
        entry.itemId = itemIdField.getText().trim();
        entry.loreContains = loreField.getText().trim();
        entry.requiredEnchants = new ArrayList<>();
        for (String part : enchantsField.getText().split(",")) {
            String s = part.trim();
            if (!s.isBlank()) entry.requiredEnchants.add(s);
        }

        try {
            entry.maxPrice = Long.parseLong(priceField.getText().trim());
        } catch (NumberFormatException e) {
            entry.maxPrice = 0;
        }

        AutoAhConfig.webhookUrl = webhookField.getText().trim();
        AutoAhConfig.save();
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
        super.render(context, mouseX, mouseY, delta);

        int centerX = width / 2;
        context.drawCenteredTextWithShadow(textRenderer, title, centerX, height / 2 - 110, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer,
                "Reguła: " + (selectedIndex + 1) + "/" + AutoAhConfig.RULES.size() + " | AutoAH: " + (AutoAhConfig.enabled ? "ON" : "OFF"),
                centerX - 150, height / 2 + 85, 0xA0FFA0);
        context.drawTextWithShadow(textRenderer,
                "Middle click = ustaw item z main-hand. Toggle key i GUI key są w keybinds.",
                centerX - 150, height / 2 + 100, 0xBBBBBB);
    }
}
