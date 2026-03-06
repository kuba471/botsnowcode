package pl.botsnow.autoah;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class RuleMatcher {
    public static Optional<RuleEntry> findMatch(ItemStack stack) {
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        List<Text> loreLines = stack.getTooltip(null, TooltipContext.BASIC);
        String tooltip = loreLines.stream().map(Text::getString).reduce("", (a, b) -> a + "\n" + b).toLowerCase(Locale.ROOT);

        for (RuleEntry rule : AutoAhConfig.RULES) {
            if (!rule.enabled) {
                continue;
            }
            if (!rule.itemId.equalsIgnoreCase(itemId)) {
                continue;
            }
            if (!rule.loreContains.isBlank() && !tooltip.contains(rule.loreContains.toLowerCase(Locale.ROOT))) {
                continue;
            }

            boolean enchantsOk = true;
            for (String enchantNeedle : rule.requiredEnchants) {
                if (!tooltip.contains(enchantNeedle.toLowerCase(Locale.ROOT))) {
                    enchantsOk = false;
                    break;
                }
            }
            if (enchantsOk) {
                return Optional.of(rule);
            }
        }

        return Optional.empty();
    }
}
