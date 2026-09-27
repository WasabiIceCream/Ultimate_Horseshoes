package net.nanaky.ultimate_horseshoes.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.nanaky.ultimate_horseshoes.HorseshoeSlot;
import net.nanaky.ultimate_horseshoes.IHorseshoesContainer;
import net.nanaky.ultimate_horseshoes.UltimateHorseshoes;
import net.nanaky.ultimate_horseshoes.config.ModConfigs;
import net.nanaky.ultimate_horseshoes.items.HorseshoesItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseScreenHandlerMixin extends AbstractMountInventoryMenu {
    protected HorseScreenHandlerMixin(int syncId, Inventory playerInventory, Container mountContainer, LivingEntity mount) {
        super(syncId, playerInventory, mountContainer, mount);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void horseshoes$addHorseshoeSlot(int syncId, Inventory playerInventory,
                                              Container inventory, AbstractHorse horse,
                                              int inventoryColumns, CallbackInfo ci) {
        if (!horse.getType().builtInRegistryHolder().is(UltimateHorseshoes.ALLOWED)) return;
        Container horseshoeContainer = ((IHorseshoesContainer) horse).horseshoes$getContainer();
        this.addSlot(new HorseshoeSlot(horseshoeContainer, horse));
    }

    // Gameoverse: only horseshoe moves are handled here; everything else goes to the menu's
    // own shift-click (which Horseman also changes). Upstream replaced all of it and assumed
    // the horseshoe slot was the last one.
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        HorseshoeSlot horseshoeSlot = null;
        for (Slot slot : this.slots) {
            if (slot instanceof HorseshoeSlot found) {
                horseshoeSlot = found;
                break;
            }
        }
        if (horseshoeSlot == null) return super.quickMoveStack(player, index);

        Slot clickedSlot = this.slots.get(index);
        if (clickedSlot == horseshoeSlot) {
            if (ModConfigs.SLOT_LOCK) return ItemStack.EMPTY;
            ItemStack stack = horseshoeSlot.getItem();
            if (stack.isEmpty()) return ItemStack.EMPTY;
            if (net.minecraft.world.item.enchantment.EnchantmentHelper.has(
                stack, net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
                return ItemStack.EMPTY;
            }
            player.getInventory().add(stack);
            horseshoeSlot.setChanged();
            return ItemStack.EMPTY;
        }

        ItemStack stack = clickedSlot.getItem();
        if (clickedSlot.container instanceof Inventory && stack.getItem() instanceof HorseshoesItem) {
            if (horseshoeSlot.isActive() && !horseshoeSlot.hasItem()) {
                horseshoeSlot.setByPlayer(stack.split(1));
                clickedSlot.setChanged();
            }
            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, index);
    }
}
