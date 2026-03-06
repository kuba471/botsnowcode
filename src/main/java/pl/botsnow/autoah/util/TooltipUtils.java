package pl.botsnow.autoah.util;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class TooltipUtils {
    public static List<Text> tooltip(ItemStack stack) {
        return stack.getTooltip(null, TooltipContext.BASIC);
    }
}
