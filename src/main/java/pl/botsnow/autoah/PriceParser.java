package pl.botsnow.autoah;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceParser {
    private static final Pattern PRICE_PATTERN = Pattern.compile("(?:^|\\D)(\\d[\\d\\s_.,]*)$");

    public static long parsePrice(ItemStack stack) {
        List<Text> loreLines = stack.getTooltip(null, TooltipContext.BASIC);
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
}
