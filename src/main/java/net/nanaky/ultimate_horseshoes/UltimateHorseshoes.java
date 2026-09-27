package net.nanaky.ultimate_horseshoes;

import static net.nanaky.ultimate_horseshoes.items.ModItems.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.nanaky.ultimate_horseshoes.compat.LegacyMigration;
import net.nanaky.ultimate_horseshoes.config.ModConfigs;
import net.nanaky.ultimate_horseshoes.items.ModItems;

public class UltimateHorseshoes implements ModInitializer {

    public static final String MOD_ID = "ultimate_horseshoes";

    // Gameoverse: upstream's pools held only horseshoe entries with 1-3 rolls, so every chest
    // of these types contained horseshoes. Now one roll at 5%, same tier weights.
    private static final float LOOT_CHANCE = 0.05F;

    public static final Identifier HORSESHOES_SPEED_ID =
            Identifier.fromNamespaceAndPath(MOD_ID, "ultimate_horseshoesspeed_bonus");
    public static final Identifier HORSESHOES_JUMP_ID =
        Identifier.fromNamespaceAndPath(MOD_ID, "ultimate_horseshoesjump_bonus");
    public static final Identifier HORSESHOES_ARMOR_ID =
            Identifier.fromNamespaceAndPath(MOD_ID, "ultimate_horseshoesarmor_bonus");

    public static final TagKey<EntityType<?>> ALLOWED = TagKey.create(
            Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "allowed_to_wear_horseshoes"));

    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ModConfigs.registerConfigs());
        ModItems.registerModItems();
        LegacyMigration.register();
        LootTableEvents.MODIFY.register((id, tableBuilder, source, registries) -> {
            if (source.isBuiltin()) {
                if (BuiltInLootTables.NETHER_BRIDGE.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(GOLDEN_HORSESHOES_ITEM).setWeight(4)));
                }
                if (BuiltInLootTables.BASTION_BRIDGE.equals(id)
                        || BuiltInLootTables.BASTION_HOGLIN_STABLE.equals(id)
                        || BuiltInLootTables.BASTION_OTHER.equals(id)
                        || BuiltInLootTables.BASTION_TREASURE.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(GOLDEN_HORSESHOES_ITEM).setWeight(8)));
                }
                if (BuiltInLootTables.VILLAGE_ARMORER.equals(id)
                        || BuiltInLootTables.VILLAGE_WEAPONSMITH.equals(id)
                        || BuiltInLootTables.VILLAGE_TOOLSMITH.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(COPPER_HORSESHOES_ITEM).setWeight(8))
                            .add(LootItem.lootTableItem(IRON_HORSESHOES_ITEM).setWeight(4)));
                }
                if (BuiltInLootTables.DESERT_PYRAMID.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(COPPER_HORSESHOES_ITEM).setWeight(8))
                            .add(LootItem.lootTableItem(IRON_HORSESHOES_ITEM).setWeight(4))
                            .add(LootItem.lootTableItem(DIAMOND_HORSESHOES_ITEM).setWeight(1)));
                            
                }
                if (BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(COPPER_HORSESHOES_ITEM).setWeight(3)));
                }
                if (BuiltInLootTables.SIMPLE_DUNGEON.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(COPPER_HORSESHOES_ITEM).setWeight(4))
                            .add(LootItem.lootTableItem(IRON_HORSESHOES_ITEM).setWeight(4))
                            .add(LootItem.lootTableItem(DIAMOND_HORSESHOES_ITEM).setWeight(1)));
                }
                if (BuiltInLootTables.END_CITY_TREASURE.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(IRON_HORSESHOES_ITEM))
                            .add(LootItem.lootTableItem(GOLDEN_HORSESHOES_ITEM))
                            .add(LootItem.lootTableItem(DIAMOND_HORSESHOES_ITEM)));
                }
                if (BuiltInLootTables.ANCIENT_CITY.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(DIAMOND_HORSESHOES_ITEM).setWeight(8)));
                }
                if (BuiltInLootTables.WOODLAND_MANSION.equals(id)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LOOT_CHANCE))
                            .add(LootItem.lootTableItem(IRON_HORSESHOES_ITEM).setWeight(8))
                            .add(LootItem.lootTableItem(DIAMOND_HORSESHOES_ITEM).setWeight(4)));
                }
            }
        });
    }
}