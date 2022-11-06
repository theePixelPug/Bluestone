package net.theepixelpug.bluestone.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.theepixelpug.bluestone.particle.BluestoneDustParticleEffect;

public class BluestoneRepeaterBlock extends AbstractBluestoneGateBlock {
    public static final BooleanProperty LOCKED;
    public static final IntProperty DELAY;

    public BluestoneRepeaterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(DELAY, 1)).with(LOCKED, false)).with(BLUESTONE_POWERED, false));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        } else {
            world.setBlockState(pos, (BlockState)state.cycle(DELAY), 3);
            return ActionResult.success(world.isClient);
        }
    }

    protected int getUpdateDelayInternal(BlockState state) {
        return (Integer)state.get(DELAY) * 2;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        return (BlockState)blockState.with(LOCKED, this.isLocked(ctx.getWorld(), ctx.getBlockPos(), blockState));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !world.isClient() && direction.getAxis() != ((Direction)state.get(FACING)).getAxis() ? (BlockState)state.with(LOCKED, this.isLocked(world, pos, state)) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean isLocked(WorldView world, BlockPos pos, BlockState state) {
        return this.getMaxInputLevelSides(world, pos, state) > 0;
    }

    protected boolean isValidInput(BlockState state) {
        return isBluestoneGate(state);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((Boolean)state.get(BLUESTONE_POWERED)) {
            Direction direction = (Direction)state.get(FACING);
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
            double e = (double)pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
            float g = -5.0F;
            if (random.nextBoolean()) {
                g = (float)((Integer)state.get(DELAY) * 2 - 1);
            }

            g /= 16.0F;
            double h = (double)(g * (float)direction.getOffsetX());
            double i = (double)(g * (float)direction.getOffsetZ());
            world.addParticle(BluestoneDustParticleEffect.BLUESTONE, d + h, e, f + i, 0.0, 0.0, 0.0);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, DELAY, LOCKED, BLUESTONE_POWERED});
    }

    static {
        LOCKED = Properties.LOCKED;
        DELAY = Properties.DELAY;
    }
}
