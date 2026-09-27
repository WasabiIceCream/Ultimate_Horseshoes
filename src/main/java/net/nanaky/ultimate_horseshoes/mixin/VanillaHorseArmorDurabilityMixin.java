package net.nanaky.ultimate_horseshoes.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.nanaky.ultimate_horseshoes.config.ModConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.core.Holder$Reference")
public class VanillaHorseArmorDurabilityMixin {

    @Shadow
    private DataComponentMap components;

    @Inject(method = "bindComponents", at = @At("TAIL"))
    private void horseshoes$patchHorseArmorDurability(DataComponentMap incomingComponents, CallbackInfo ci) {
        if (!ModConfigs.VANILLA_ARMOR_DURABILITY) return;

        Holder.Reference<?> self = (Holder.Reference<?>) (Object) this;
        if (!(self.value() instanceof Item item)) return;

        // Gameoverse: also enchantable (the armour material's own value) and repairable with
        // its material, so it works with the enchanting rework and can be fixed once broken
        int maxDamage = 0;
        int enchantability;
        Item repairItem;
        if      (item == Items.IRON_HORSE_ARMOR)      { maxDamage = 195; enchantability = 9;  repairItem = Items.IRON_INGOT; }
        else if (item == Items.GOLDEN_HORSE_ARMOR)    { maxDamage = 91;  enchantability = 25; repairItem = Items.GOLD_INGOT; }
        else if (item == Items.DIAMOND_HORSE_ARMOR)   { maxDamage = 429; enchantability = 10; repairItem = Items.DIAMOND; }
        else if (item == Items.NETHERITE_HORSE_ARMOR) { maxDamage = 481; enchantability = 15; repairItem = Items.NETHERITE_INGOT; }
        else if (item == Items.LEATHER_HORSE_ARMOR)   { maxDamage = 110; enchantability = 15; repairItem = Items.LEATHER; }
        else if (item == Items.COPPER_HORSE_ARMOR)    { maxDamage = 143; enchantability = 8;  repairItem = Items.COPPER_INGOT; }
        else return;

        this.components = DataComponentMap.builder()
            .addAll(this.components)
            .set(DataComponents.MAX_DAMAGE, maxDamage)
            .set(DataComponents.DAMAGE, 0)
            .set(DataComponents.ENCHANTABLE, new net.minecraft.world.item.enchantment.Enchantable(enchantability))
            .set(DataComponents.REPAIRABLE, new net.minecraft.world.item.enchantment.Repairable(
                net.minecraft.core.HolderSet.direct(repairItem.builtInRegistryHolder())))
            .build();
    }
}