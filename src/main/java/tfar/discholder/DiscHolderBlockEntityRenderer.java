package tfar.discholder;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

import static tfar.discholder.DiscHolderBlock.getSlot;

public class DiscHolderBlockEntityRenderer extends TileEntityRenderer<DiscHolderBlockEntity> {

    public DiscHolderBlockEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(DiscHolderBlockEntity be, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        double shiftX;
        double shiftY;
        double shiftZ;
        Direction facing = be.getBlockState().get(HorizontalBlock.HORIZONTAL_FACING);
        boolean isXAxis = facing.getAxis() == Direction.Axis.X;

        for (int i = 0; i < 7; i++) {
            ItemStack item = be.records.getStackInSlot(i);
            if (item.isEmpty()) continue;
            //double blockScale = 1;
            shiftX = isXAxis ? .5 - .03125 : .125 + .125 * i;
            shiftY = .375;
            shiftZ = isXAxis ? .125 + .125 * i : .5 + .03125;
            matrixStackIn.push();
            matrixStackIn.translate(shiftX, shiftY, shiftZ);
            int i1 = isXAxis ? 90 : 0;
               matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i1 * 360.0F / 8.0F));
            Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
            matrixStackIn.pop();
        }
        RayTraceResult rayTraceResult = Minecraft.getInstance().objectMouseOver;
        if (rayTraceResult instanceof BlockRayTraceResult && rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult rayTraceResult1 = (BlockRayTraceResult) rayTraceResult;
            if (be.getBlockState().getBlock() instanceof DiscHolderBlock) {
                Vector3d vec3d = rayTraceResult1.getHitVec();
                int slot = getSlot(vec3d,facing);
                if (slot != -1) {
                    ItemStack stackInSlot = be.records.getStackInSlot(slot);
                    if (!stackInSlot.isEmpty()) {
                        IFormattableTextComponent str = ((MusicDiscItem) stackInSlot.getItem()).getDescription();
                        this.renderName(be, str, matrixStackIn, bufferIn, combinedLightIn);
                    }
                }
            }
        }
    }

    protected void renderName(DiscHolderBlockEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        //double d0 = this.renderManager.squareDistanceTo(entityIn);
        //if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(entityIn, d0)) {
        float f = 0.5F;
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, (double) f, 0.0D);
        matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
        int j = (int) (f1 * 255.0F) << 24;
        FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
        float f2 = (float) (-fontrenderer.getStringPropertyWidth(displayNameIn) / 2);
        //  fontrenderer.func_243247_a(displayNameIn, f2, 0, 553648127, false, matrix4f, bufferIn, flag, j, packedLightIn);
        fontrenderer.func_243247_a(displayNameIn, f2, 0, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
        // }
        matrixStackIn.pop();
    }
}
