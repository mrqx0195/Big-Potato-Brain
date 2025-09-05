package net.mrqx.potato.brain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BlockBigBrain extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BlockBigBrain() {
        super(Properties.of()
                .mapColor(MapColor.PLANT).strength(2, 3).sound(SoundType.SLIME_BLOCK)
                .noOcclusion().instrument(NoteBlockInstrument.BASS)
                .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
                .isSuffocating((blockState, blockGetter, blockPos) -> false)
                .isViewBlocking((blockState, blockGetter, blockPos) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (player.getMainHandItem().isEmpty()) {
            if (blockentity instanceof BlockEntityBigBrain blockEntityBigBrain && blockEntityBigBrain.exp > 0) {
                if (level instanceof ServerLevel) {
                    ItemStack itemStack = BigPotatoBrainMod.POTATO_OF_KNOWLEDGE.get().getDefaultInstance();
                    itemStack.getOrCreateTag().putInt(ItemKnowledgePotato.KNOWLEDGE_POTATO_KEY, blockEntityBigBrain.exp);
                    player.setItemSlot(EquipmentSlot.MAINHAND, itemStack.copy());
                    blockEntityBigBrain.exp = 0;
                    return InteractionResult.SUCCESS;
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean movedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof BlockEntityBigBrain blockEntityBigBrain && blockEntityBigBrain.exp > 0) {
                if (pLevel instanceof ServerLevel) {
                    ItemStack itemStack = BigPotatoBrainMod.POTATO_OF_KNOWLEDGE.get().getDefaultInstance();
                    itemStack.getOrCreateTag().putInt(ItemKnowledgePotato.KNOWLEDGE_POTATO_KEY, blockEntityBigBrain.exp);
                    Vec3 vec3 = Vec3.atLowerCornerWithOffset(pPos, 0.5D, 1.01D, 0.5D).offsetRandom(pLevel.random, 0.7F);
                    ItemEntity itemEntity = new ItemEntity(pLevel, vec3.x(), vec3.y(), vec3.z(), itemStack.copy());
                    itemEntity.setDefaultPickUpDelay();
                    pLevel.addFreshEntity(itemEntity);
                }
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, movedByPiston);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntityBigBrain(blockPos, blockState);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createFurnaceTicker(pLevel, pBlockEntityType, (BlockEntityType<? extends BlockEntityBigBrain>) BigPotatoBrainMod.BIG_BRAIN_BLOCK_ENTITY.get());
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createFurnaceTicker(Level pLevel, BlockEntityType<T> pServerType, BlockEntityType<? extends BlockEntityBigBrain> pClientType) {
        return pLevel.isClientSide ? null : createTickerHelper(pServerType, pClientType, BlockEntityBigBrain::serverTick);
    }
}
