package net.nanaky.ultimate_horseshoes.items;

import static net.nanaky.ultimate_horseshoes.UltimateHorseshoes.MOD_ID;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Repairable;
import net.nanaky.ultimate_horseshoes.config.ModConfigs;

public class ModItems {

    // Gameoverse: repair through vanilla's armour repair tags, like the matching armour, so
    // datapacks that change a tier's material (the Iron Forge Gate puts iron on Iron Sheets)
    // apply to horseshoes too. Upstream named one item per tier.
    private static Repairable repairable(net.minecraft.tags.TagKey<Item> tag) {
        return new Repairable(BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM).getOrThrow(tag));
    }

    public static final HorseshoesItem COPPER_HORSESHOES_ITEM = registerItem("copper_horseshoes",
        new HorseshoesItem(
            (float) ModConfigs.COPPER_HORSESHOES_SPEED,
            (float) ModConfigs.COPPER_HORSESHOES_JUMP,
            (float) ModConfigs.COPPER_HORSESHOES_ARMOR,
            new Item.Properties().durability(143)
                .component(DataComponents.REPAIRABLE, repairable(net.minecraft.tags.ItemTags.REPAIRS_COPPER_ARMOR))
                .enchantable(8)
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "copper_horseshoes"))),
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/horse/armor/copper_horseshoes.png")));

    public static final HorseshoesItem IRON_HORSESHOES_ITEM = registerItem("iron_horseshoes",
        new HorseshoesItem(
            (float) ModConfigs.IRON_HORSESHOES_SPEED,
            (float) ModConfigs.IRON_HORSESHOES_JUMP,
            (float) ModConfigs.IRON_HORSESHOES_ARMOR,
            new Item.Properties().durability(195)
                .component(DataComponents.REPAIRABLE, repairable(net.minecraft.tags.ItemTags.REPAIRS_IRON_ARMOR))
                .enchantable(9)
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "iron_horseshoes"))),
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/horse/armor/iron_horseshoes.png")));

    public static final HorseshoesItem GOLDEN_HORSESHOES_ITEM = registerItem("golden_horseshoes",
        new HorseshoesItem(
            (float) ModConfigs.GOLDEN_HORSESHOES_SPEED,
            (float) ModConfigs.GOLDEN_HORSESHOES_JUMP,
            (float) ModConfigs.GOLDEN_HORSESHOES_ARMOR,
            new Item.Properties().durability(91)
                .component(DataComponents.REPAIRABLE, repairable(net.minecraft.tags.ItemTags.REPAIRS_GOLD_ARMOR))
                .enchantable(25)
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "golden_horseshoes"))),
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/horse/armor/golden_horseshoes.png")));

    public static final HorseshoesItem DIAMOND_HORSESHOES_ITEM = registerItem("diamond_horseshoes",
        new HorseshoesItem(
            (float) ModConfigs.DIAMOND_HORSESHOES_SPEED,
            (float) ModConfigs.DIAMOND_HORSESHOES_JUMP,
            (float) ModConfigs.DIAMOND_HORSESHOES_ARMOR,
            new Item.Properties().durability(429)
                .component(DataComponents.REPAIRABLE, repairable(net.minecraft.tags.ItemTags.REPAIRS_DIAMOND_ARMOR))
                .enchantable(10)
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "diamond_horseshoes"))),
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/horse/armor/diamond_horseshoes.png")));

    public static final HorseshoesItem NETHERITE_HORSESHOES_ITEM = registerItem("netherite_horseshoes",
        new HorseshoesItem(
            (float) ModConfigs.NETHERITE_HORSESHOES_SPEED,
            (float) ModConfigs.NETHERITE_HORSESHOES_JUMP,
            (float) ModConfigs.NETHERITE_HORSESHOES_ARMOR,
            new Item.Properties().durability(481)
                .component(DataComponents.REPAIRABLE, repairable(net.minecraft.tags.ItemTags.REPAIRS_NETHERITE_ARMOR))
                .enchantable(15)
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "netherite_horseshoes"))),
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/horse/armor/netherite_horseshoes.png")));

    private static HorseshoesItem registerItem(String name, HorseshoesItem item) {
        return (HorseshoesItem) Registry.register(BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(MOD_ID, name), item);
    }

    public static void registerModItems() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(g -> g.accept(COPPER_HORSESHOES_ITEM));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(g -> g.accept(IRON_HORSESHOES_ITEM));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(g -> g.accept(GOLDEN_HORSESHOES_ITEM));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(g -> g.accept(DIAMOND_HORSESHOES_ITEM));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(g -> g.accept(NETHERITE_HORSESHOES_ITEM));
    }
}