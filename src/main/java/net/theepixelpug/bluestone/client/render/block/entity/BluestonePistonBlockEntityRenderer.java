package net.theepixelpug.bluestone.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.theepixelpug.bluestone.block.BluestonePistonBlock;
import net.theepixelpug.bluestone.block.BluestonePistonHeadBlock;
import net.theepixelpug.bluestone.block.ModBlocks;
import net.theepixelpug.bluestone.block.entity.BluestonePistonBlockEntity;

@Environment(value= EnvType.CLIENT)
public class BluestonePistonBlockEntityRenderer
        implements BlockEntityRenderer<BluestonePistonBlockEntity> {
    private final BlockRenderManager manager;

    public BluestonePistonBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    @Override
    public void render(BluestonePistonBlockEntity pistonBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        World world = pistonBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.getMovementDirection().getOpposite());
        BlockState blockState = pistonBlockEntity.getPushedBlock();
        if (blockState.isAir()) {
            return;
        }
        BlockModelRenderer.enableBrightnessCache();
        matrixStack.push();
        matrixStack.translate(pistonBlockEntity.getRenderOffsetX(f), pistonBlockEntity.getRenderOffsetY(f), pistonBlockEntity.getRenderOffsetZ(f));
        if (blockState.isOf(ModBlocks.BLUESTONE_PISTON_HEAD) && pistonBlockEntity.getProgress(f) <= 4.0f) {
            blockState = (BlockState)blockState.with(BluestonePistonHeadBlock.SHORT, pistonBlockEntity.getProgress(f) <= 0.5f);
            this.renderModel(blockPos, blockState, matrixStack, vertexConsumerProvider, world, false, j);
        } else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
            PistonType pistonType = blockState.isOf(ModBlocks.BLUESTONE_STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState2 = (BlockState)((BlockState)ModBlocks.BLUESTONE_PISTON_HEAD.getDefaultState().with(BluestonePistonHeadBlock.TYPE, pistonType)).with(BluestonePistonHeadBlock.FACING, blockState.get(PistonBlock.FACING));
            blockState2 = (BlockState)blockState2.with(BluestonePistonHeadBlock.SHORT, pistonBlockEntity.getProgress(f) >= 0.5f);
            this.renderModel(blockPos, blockState2, matrixStack, vertexConsumerProvider, world, false, j);
            BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.getMovementDirection());
            matrixStack.pop();
            matrixStack.push();
            blockState = (BlockState)blockState.with(BluestonePistonBlock.EXTENDED, true);
            this.renderModel(blockPos2, blockState, matrixStack, vertexConsumerProvider, world, true, j);
        } else {
            this.renderModel(blockPos, blockState, matrixStack, vertexConsumerProvider, world, false, j);
        }
        matrixStack.pop();
        BlockModelRenderer.disableBrightnessCache();
    }

    private void renderModel(BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, boolean cull, int overlay) {
        RenderLayer renderLayer = RenderLayers.getMovingBlockLayer(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        this.manager.getModelRenderer().render(world, this.manager.getModel(state), state, pos, matrices, vertexConsumer, cull, Random.create(), state.getRenderingSeed(pos), overlay);
    }

    @Override
    public int getRenderDistance() {
        return 68;
    }
}
