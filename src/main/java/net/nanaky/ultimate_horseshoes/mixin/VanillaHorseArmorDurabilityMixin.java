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

        // Gameoverse: also enchantable (the armour material's own value) and repairable through
        // its armour repair tag, so it works with the enchanting rework and can be fixed once broken
        int maxDamage = 0;
        int enchantability;
        net.minecraft.tags.TagKey<Item> repairTag;
        if      (item == Items.IRON_HORSE_ARMOR)      { maxDamage = 195; enchantability = 9;  repairTag = net.minecraft.tags.ItemTags.REPAIRS_IRON_ARMOR; }
        else if (item == Items.GOLDEN_HORSE_ARMOR)    { maxDamage = 91;  enchantability = 25; repairTag = net.minecraft.tags.ItemTags.REPAIRS_GOLD_ARMOR; }
        else if (item == Items.DIAMOND_HORSE_ARMOR)   { maxDamage = 429; enchantability = 10; repairTag = net.minecraft.tags.ItemTags.REPAIRS_DIAMOND_ARMOR; }
        else if (item == Items.NETHERITE_HORSE_ARMOR) { maxDamage = 481; enchantability = 15; repairTag = net.minecraft.tags.ItemTags.REPAIRS_NETHERITE_ARMOR; }
        else if (item == Items.LEATHER_HORSE_ARMOR)   { maxDamage = 110; enchantability = 15; repairTag = net.minecraft.tags.ItemTags.REPAIRS_LEATHER_ARMOR; }
        else if (item == Items.COPPER_HORSE_ARMOR)    { maxDamage = 143; enchantability = 8;  repairTag = net.minecraft.tags.ItemTags.REPAIRS_COPPER_ARMOR; }
        else return;

        this.components = DataComponentMap.builder()
            .addAll(this.components)
            .set(DataComponents.MAX_DAMAGE, maxDamage)
            .set(DataComponents.DAMAGE, 0)
            .set(DataComponents.ENCHANTABLE, new net.minecraft.world.item.enchantment.Enchantable(enchantability))
            // The registry is frozen here; vanilla armour already created these tags' holder
            // sets during bootstrap, so this returns the existing one (a bootstrap lookup throws)
            .set(DataComponents.REPAIRABLE, new net.minecraft.world.item.enchantment.Repairable(
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getOrThrow(repairTag)))
            .build();
    }
}