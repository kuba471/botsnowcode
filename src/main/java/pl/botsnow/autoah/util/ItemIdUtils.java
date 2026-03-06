package pl.botsnow.autoah.util;

import net.minecraft.registry.Registries;
import net.minecraft.item.ItemStack;

public class ItemIdUtils {
    public static String id(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).toString();
    }
}
