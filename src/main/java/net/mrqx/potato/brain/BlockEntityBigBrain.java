package net.mrqx.potato.brain;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockEntityBigBrain extends BlockEntity {
    public int exp = 0;

    public BlockEntityBigBrain(BlockPos pos, BlockState blockState) {
        super(BigPotatoBrainMod.BIG_BRAIN_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("exp", this.exp);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.exp = tag.getInt("exp");
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState ignoreState, BlockEntityBigBrain pBlockEntity) {
        if (Config.ABSORB_EXP.get()) {
            pLevel.getEntitiesOfClass(ExperienceOrb.class, new AABB(pPos, pPos.offset(1, 1, 1)).inflate(Config.RANGE.get())).forEach(experienceOrb -> {
                pBlockEntity.exp += experienceOrb.getValue();
                experienceOrb.discard();
                pLevel.playSound(experienceOrb, pPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1, 1);
            });
        } else {
            pLevel.getEntitiesOfClass(ExperienceOrb.class, new AABB(pPos, pPos.offset(1, 1, 1)).inflate(0.05)).forEach(experienceOrb -> {
                pBlockEntity.exp += experienceOrb.getValue();
                experienceOrb.discard();
                pLevel.playSound(experienceOrb, pPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1, 1);
            });
            pLevel.getEntitiesOfClass(ExperienceOrb.class, new AABB(pPos, pPos.offset(1, 1, 1)).inflate(Config.RANGE.get())).forEach(experienceOrb -> {
                experienceOrb.noPhysics = true;
                experienceOrb.setOnGround(false);
                experienceOrb.setNoGravity(true);
                Vec3 vec3 = new Vec3(pPos.getCenter().x - experienceOrb.getX(), pPos.getCenter().y - experienceOrb.getY(), pPos.getCenter().z - experienceOrb.getZ());
                double d0 = vec3.lengthSqr();
                double d1 = 1.0D - Math.sqrt(d0) / 8.0D;
                experienceOrb.setDeltaMovement(experienceOrb.getDeltaMovement().add(vec3.normalize().scale(d1 * d1)).normalize().scale(0.5));
                experienceOrb.hurtMarked = true;
            });
        }
        while (pBlockEntity.exp >= Config.BRAIN_MAX.get()) {
            ItemStack itemStack = BigPotatoBrainMod.POTATO_OF_KNOWLEDGE.get().getDefaultInstance();
            itemStack.getOrCreateTag().putInt(ItemKnowledgePotato.KNOWLEDGE_POTATO_KEY, Config.BRAIN_MAX.get());
            Vec3 vec3 = Vec3.atLowerCornerWithOffset(pPos, 0.5D, 1.01D, 0.5D).offsetRandom(pLevel.random, 0.7F);
            ItemEntity itemEntity = new ItemEntity(pLevel, vec3.x(), vec3.y(), vec3.z(), itemStack.copy());
            itemEntity.setDefaultPickUpDelay();
            pLevel.addFreshEntity(itemEntity);
            pBlockEntity.exp -= Config.BRAIN_MAX.get();
            pLevel.playSound(null, pPos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1, 1);
        }
    }
}
