package net.nanaky.ultimate_horseshoes.mixin;

import net.nanaky.ultimate_horseshoes.UltimateHorseshoes;
import net.nanaky.ultimate_horseshoes.IHorseshoesContainer;
import net.nanaky.ultimate_horseshoes.config.ModConfigs;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMountInventoryScreen.class)
public abstract class AbstractHorseScreenMixin<M extends AbstractContainerMenu>
        extends AbstractContainerScreen<M> {

    private static final Identifier HORSESHOES_TEXTURE =
            Identifier.fromNamespaceAndPath("ultimate_horseshoes", "textures/gui/container/horse.png");
        
    private static final Identifier SHEARS_HINT_TEXTURE =
        Identifier.fromNamespaceAndPath("ultimate_horseshoes", "textures/gui/slot_lock.png");

    protected AbstractHorseScreenMixin(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Shadow protected net.minecraft.world.entity.LivingEntity mount;

    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void horseshoes$renderHorseshoeSlot(GuiGraphicsExtractor graphics,
                                                int mouseX, int mouseY, float partialTick,
                                                CallbackInfo ci) {
        if (!(this.mount instanceof AbstractHorse horse)) return;
        if (!horse.getType().builtInRegistryHolder().is(UltimateHorseshoes.ALLOWED)) return;
        if (!horse.canUseSlot(EquipmentSlot.FEET)) return;

        // Gameoverse: the slot sits outside the panel's left edge (see HorseshoeSlot), on a
        // small backing panel joined to the main one
        int xo = this.leftPos + net.nanaky.ultimate_horseshoes.HorseshoeSlot.X - 1;
        int yo = this.topPos + net.nanaky.ultimate_horseshoes.HorseshoeSlot.Y - 1;
        int px = xo - 4, py = yo - 4, pw = this.leftPos + 3 - px, ph = 26;
        graphics.fill(px, py, px + pw, py + ph, 0xFF000000);
        graphics.fill(px + 1, py + 1, px + pw, py + ph - 1, 0xFFC6C6C6);
        graphics.fill(px + 1, py + 1, px + pw, py + 2, 0xFFFFFFFF);
        graphics.fill(px + 1, py + 1, px + 2, py + ph - 1, 0xFFFFFFFF);
        graphics.fill(px + 2, py + ph - 2, px + pw, py + ph - 1, 0xFF555555);

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            HORSESHOES_TEXTURE,
            xo, yo,
            54, 220,
            18, 18,
            256, 256
            );

        ItemStack equipped = ((IHorseshoesContainer) horse).horseshoes$getContainer().getItem(0);
        if (!equipped.isEmpty() & (ModConfigs.SLOT_LOCK)) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                SHEARS_HINT_TEXTURE,
                xo+1, yo+1,
                0, 0,
                16, 16,
                16, 16
            );
        }
    }    
}