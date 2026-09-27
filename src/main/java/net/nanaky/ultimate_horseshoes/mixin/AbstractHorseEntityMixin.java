package net.nanaky.ultimate_horseshoes.mixin;

import net.nanaky.ultimate_horseshoes.UltimateHorseshoes;
import net.nanaky.ultimate_horseshoes.IHorseshoesContainer;
import net.nanaky.ultimate_horseshoes.config.ModConfigs;
import net.nanaky.ultimate_horseshoes.items.HorseshoesItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.player.Player;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseEntityMixin extends Animal implements IHorseshoesContainer {

    @Unique
    private final SimpleContainer horseshoes$container = new SimpleContainer(1);

    @Unique
    private boolean horseshoes$hadHorseshoes = false;

    protected AbstractHorseEntityMixin(EntityType<? extends Animal> entityType, net.minecraft.world.level.Level world) {
        super(entityType, world);
    }

    @Override
    public SimpleContainer horseshoes$getContainer() {
        return horseshoes$container;
    }

    @Unique
    private boolean horseshoes$hasHorseshoes() {
        return horseshoes$container.getItem(0).getItem() instanceof HorseshoesItem;
    }

    // Gameoverse: horseshoes at 0 durability stay in the slot (Simple Smithing Overhaul marks
    // them Broken) and count as absent until repaired: no bonuses, no enchantment effects
    @Unique
    private boolean horseshoes$activeHorseshoes() {
        if (!horseshoes$hasHorseshoes()) return false;
        ItemStack shoes = horseshoes$container.getItem(0);
        return !shoes.isDamageableItem() || shoes.getDamageValue() < shoes.getMaxDamage();
    }

    @Unique
    private float horseshoes$distanceAccumulator = 0f;

    @Unique
    private double horseshoes$lastX;
    
    @Unique  
    private double horseshoes$lastZ;

    @Unique
    private boolean horseshoes$posInitialized = false;

    @Inject(method = "playStepSound", at = @At("HEAD"))
    private void horseshoes$soulSpeedDurability(BlockPos pos, net.minecraft.world.level.block.state.BlockState state, CallbackInfo ci) {
        if (this.level().isClientSide()) return;
        if (!horseshoes$activeHorseshoes()) return;

        ItemStack shoes = horseshoes$container.getItem(0);
        int soulSpeedLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
            this.level().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SOUL_SPEED),
            shoes
        );

        if (soulSpeedLevel > 0) {
            net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> soulSpeedBlocks =
                net.minecraft.tags.BlockTags.SOUL_SPEED_BLOCKS;
            if (state.is(soulSpeedBlocks)) {
                horseshoes$damageHorseshoe(soulSpeedLevel);
            }
        }
    }

    @Unique
    private void horseshoes$damageHorseshoe(int amount) {
        if (!ModConfigs.HORSESHOES_DURABILITY) return;
        if (!this.onGround()) return;
        if (this.getVehicle() instanceof net.minecraft.world.entity.vehicle.boat.Boat) return;
        if (this.getVehicle() instanceof net.minecraft.world.entity.vehicle.minecart.Minecart) return;
        ItemStack shoes = horseshoes$container.getItem(0);
        if (shoes.isEmpty()) return;

        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        // Gameoverse: vanilla's damage path, so Unbreaking applies as the enchanting rework
        // tunes it and Simple Smithing Overhaul keeps the item (Broken) instead of deleting it
        boolean wasActive = horseshoes$activeHorseshoes();
        shoes.hurtAndBreak(amount, serverLevel, (net.minecraft.server.level.ServerPlayer) null, item -> {});
        if (wasActive && !horseshoes$activeHorseshoes()) {
            this.playSound(net.minecraft.sounds.SoundEvents.ITEM_BREAK.value(), 0.8F, 1.0F);
        }
        horseshoes$container.setChanged();
    }

    @Unique
    private void horseshoes$applyModifiers() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance jump = this.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        if (speed == null || jump == null || armor == null) return;

        speed.removeModifier(UltimateHorseshoes.HORSESHOES_SPEED_ID);
        jump.removeModifier(UltimateHorseshoes.HORSESHOES_JUMP_ID);
        armor.removeModifier(UltimateHorseshoes.HORSESHOES_ARMOR_ID);

        if (horseshoes$activeHorseshoes()) {
            ItemStack stack = horseshoes$container.getItem(0);
            HorseshoesItem item = (HorseshoesItem) stack.getItem();

            float speedBonus = item.getSpeedBonus();
            float jumpBonus = item.getJumpBonus();
            float armorBonus = item.getArmorBonus();

            speed.addTransientModifier(new AttributeModifier(
                UltimateHorseshoes.HORSESHOES_SPEED_ID, speedBonus, AttributeModifier.Operation.ADD_VALUE));
            jump.addTransientModifier(new AttributeModifier(
                UltimateHorseshoes.HORSESHOES_JUMP_ID, jumpBonus, AttributeModifier.Operation.ADD_VALUE));
            armor.addTransientModifier(new AttributeModifier(
                UltimateHorseshoes.HORSESHOES_ARMOR_ID, armorBonus, AttributeModifier.Operation.ADD_VALUE));

            this.setItemSlot(EquipmentSlot.FEET, stack.copy());
            this.setDropChance(EquipmentSlot.FEET, 0.0F);
        } else {
            this.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void horseshoes$tick(CallbackInfo ci) {
        if (this.level().isClientSide()) return;

        boolean hasNow = horseshoes$activeHorseshoes();
        if (hasNow != horseshoes$hadHorseshoes) {
            if (hasNow && this.tickCount > 20) {
                this.playSound(SoundEvents.HORSE_ARMOR.value(), 0.5F, 1.0F);
            }
            horseshoes$applyModifiers();
            horseshoes$hadHorseshoes = hasNow;
        }

        if (hasNow && ModConfigs.HORSESHOES_DURABILITY) {
            if (!horseshoes$posInitialized) {
                horseshoes$lastX = this.getX();
                horseshoes$lastZ = this.getZ();
                horseshoes$posInitialized = true;
            }

            int currentBlockX = net.minecraft.util.Mth.floor(this.getX());
            int currentBlockZ = net.minecraft.util.Mth.floor(this.getZ());
            int lastBlockX = net.minecraft.util.Mth.floor(horseshoes$lastX);
            int lastBlockZ = net.minecraft.util.Mth.floor(horseshoes$lastZ);

            if (currentBlockX != lastBlockX || currentBlockZ != lastBlockZ) {
                horseshoes$distanceAccumulator += 1;
                horseshoes$lastX = this.getX();
                horseshoes$lastZ = this.getZ();
            }

            // Gameoverse: Unbreaking is applied once, by vanilla's damage path in damageHorseshoe
            float threshold = ModConfigs.HORSESHOES_DURABILITY_THRESHOLD;

            if (horseshoes$distanceAccumulator >= threshold) {
                horseshoes$distanceAccumulator -= threshold;
                horseshoes$damageHorseshoe(1);
            }
        }
    }

    @Inject(method = "canUseSlot", at = @At("HEAD"), cancellable = true)
    private void horseshoes$allowFeetSlot(EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot == EquipmentSlot.FEET && this.getType().builtInRegistryHolder().is(UltimateHorseshoes.ALLOWED)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void horseshoes$shearHorseshoe(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.level().isClientSide()) return;
        if (!this.getType().builtInRegistryHolder().is(UltimateHorseshoes.ALLOWED)) return;

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.is(Items.SHEARS)) return;
        if (!horseshoes$hasHorseshoes()) return;
        if (!player.isShiftKeyDown()) return;

        ItemStack horseshoes = horseshoes$container.getItem(0).copy();
        if (EnchantmentHelper.has(horseshoes, net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
            cir.setReturnValue(InteractionResult.PASS);
                return;
        }

        horseshoes$container.setItem(0, ItemStack.EMPTY);

        this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
        this.playSound(SoundEvents.HORSE_ARMOR_UNEQUIP.value(), 0.5F, 1.2F);

        heldItem.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

        this.spawnAtLocation((ServerLevel) this.level(), horseshoes);

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void horseshoes$equipHorseshoe(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.level().isClientSide()) return;
        if (!this.getType().builtInRegistryHolder().is(UltimateHorseshoes.ALLOWED)) return;

        ItemStack heldItem = player.getItemInHand(hand);
        if (!(heldItem.getItem() instanceof HorseshoesItem)) return;
        if (horseshoes$hasHorseshoes()) return;
        if (!player.isShiftKeyDown()) return;

        horseshoes$container.setItem(0, heldItem.copyWithCount(1));
        heldItem.consume(1, player);

        this.playSound(SoundEvents.HORSE_ARMOR.value(), 0.5F, 1.2F);

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(method = "dropEquipment", at = @At("TAIL"))
    private void horseshoes$dropOnDeath(ServerLevel level, CallbackInfo ci) {
        if (horseshoes$hasHorseshoes()) {
            ItemStack horseshoes = horseshoes$container.getItem(0);
            if (!horseshoes.isEmpty()) {
                if (!EnchantmentHelper.has(horseshoes, 
                    net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                this.spawnAtLocation(level, horseshoes);
                }
                horseshoes$container.setItem(0, ItemStack.EMPTY);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    protected void horseshoes$write(ValueOutput output, CallbackInfo ci) {
        ItemStack stack = horseshoes$container.getItem(0);
        if (!stack.isEmpty()) {
            ItemStack.CODEC.encodeStart(
                this.registryAccess().createSerializationContext(NbtOps.INSTANCE), stack
            ).ifSuccess(tag -> output.store("ultimate_horseshoesItem", CompoundTag.CODEC, (CompoundTag) tag));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    protected void horseshoes$read(ValueInput input, CallbackInfo ci) {
        input.read("ultimate_horseshoesItem", CompoundTag.CODEC).ifPresent(tag ->
            ItemStack.CODEC.parse(
                this.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag
            ).ifSuccess(stack -> {
                if (stack.getItem() instanceof HorseshoesItem) {
                    horseshoes$container.setItem(0, stack);
                }
            })
        );
    }
}