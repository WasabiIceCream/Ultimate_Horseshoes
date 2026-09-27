package net.nanaky.ultimate_horseshoes;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.nanaky.ultimate_horseshoes.config.ModConfigs;
import net.nanaky.ultimate_horseshoes.items.HorseshoesItem;

/**
 * The horseshoe slot in a horse's inventory. A named class so the menu and screen can find it
 * wherever it lands in the slot list (Gameoverse: Horseman adds a Lead slot to the same menu,
 * so "the last slot" isn't reliably this one).
 */
public class HorseshoeSlot extends Slot {
    // Gameoverse: outside the panel's left edge, level with the third left-column slot, which
    // Horseman uses for its Lead slot (both mods put their slot at 8,54 before)
    public static final int X = -21;
    public static final int Y = 54;

    private final AbstractHorse horse;

    public HorseshoeSlot(Container container, AbstractHorse horse) {
        super(container, 0, X, Y);
        this.horse = horse;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof HorseshoesItem;
    }

    @Override
    public boolean mayPickup(Player player) {
        return !ModConfigs.SLOT_LOCK;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isActive() {
        return horse.isAlive();
    }
}
