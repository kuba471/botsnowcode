package pl.botsnow.autoah.loop;

import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class LoopTargetResolver {
    public static int findSlotWithItem(ScreenHandler handler, String itemId) {
        for (int i = 0; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);
            if (slot.getStack().isEmpty()) continue;
            Identifier id = Registries.ITEM.getId(slot.getStack().getItem());
            if (itemId.equals(id.toString())) return i;
        }
        return -1;
    }
}
