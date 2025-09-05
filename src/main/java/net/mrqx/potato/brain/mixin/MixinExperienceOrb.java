package net.mrqx.potato.brain.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperienceOrb extends Entity {
    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique
    private static final EntityDataAccessor<Boolean> BIG_BRAIN_NO_PHYSICS = SynchedEntityData.defineId(ExperienceOrb.class, EntityDataSerializers.BOOLEAN);

    public MixinExperienceOrb(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData()V", at = @At("HEAD"))
    private void injectDefineSynchedData(CallbackInfo ci) {
        this.entityData.define(BIG_BRAIN_NO_PHYSICS, false);
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    private void injectAddAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("bigBrain.noPhysics", this.entityData.get(BIG_BRAIN_NO_PHYSICS));
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    private void injectReadAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        this.entityData.set(BIG_BRAIN_NO_PHYSICS, compound.getBoolean("bigBrain.noPhysics"));
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void injectTick(CallbackInfo ci) {
        if (this.level().isClientSide) {
            this.noPhysics = this.entityData.get(BIG_BRAIN_NO_PHYSICS);
        } else {
            this.entityData.set(BIG_BRAIN_NO_PHYSICS, this.noPhysics);
        }
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/phys/AABB;)Z"))
    private boolean redirectMoveTowardsClosestSpace(Level instance, AABB aabb) {
        if (this.noPhysics) {
            return true;
        }
        return instance.noCollision(aabb);
    }
}
