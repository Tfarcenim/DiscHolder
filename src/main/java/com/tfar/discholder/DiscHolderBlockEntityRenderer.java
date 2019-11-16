package com.tfar.discholder;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import static com.tfar.discholder.DiscHolderBlock.getSlot;

public class DiscHolderBlockEntityRenderer extends TileEntityRenderer<DiscHolderBlockEntity> {

  @Override
  public void render(DiscHolderBlockEntity be, double x, double y, double z, float partialTicks, int destroyStage) {
    if (this.rendererDispatcher.renderInfo != null && be.getDistanceSq(this.rendererDispatcher.renderInfo.getProjectedView().x, this.rendererDispatcher.renderInfo.getProjectedView().y, this.rendererDispatcher.renderInfo.getProjectedView().z) < 128d) {

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
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translated(x + shiftX, y + shiftY, z + shiftZ);
        if (!isXAxis)
          GlStateManager.rotated(90, 0, 1, 0);
        Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
      }
      this.drawNameplate(be, "", x, y, z, 12);
    }
  }

  @Override
  protected void drawNameplate(DiscHolderBlockEntity te, String str, double x, double y, double z, int maxDistance) {
    ActiveRenderInfo activerenderinfo = this.rendererDispatcher.renderInfo;
    double d0 = te.getDistanceSq(activerenderinfo.getProjectedView().x, activerenderinfo.getProjectedView().y, activerenderinfo.getProjectedView().z);
    if (!(d0 > (double) (maxDistance * maxDistance))) {
      float f = activerenderinfo.getYaw();
      float f1 = activerenderinfo.getPitch();
      RayTraceResult rayTraceResult = Minecraft.getInstance().objectMouseOver;
      if (rayTraceResult instanceof BlockRayTraceResult && rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
        BlockRayTraceResult rayTraceResult1 = (BlockRayTraceResult) rayTraceResult;
        if (getWorld().getBlockState(rayTraceResult1.getPos()).getBlock() instanceof DiscHolderBlock) {
          Vec3d vec3d = rayTraceResult1.getHitVec();
          Direction facing = te.getBlockState().get(HorizontalBlock.HORIZONTAL_FACING);
          double inc = (facing == Direction.NORTH || facing == Direction.SOUTH) ? vec3d.x % 1 : vec3d.z % 1;
          int slot = getSlot(inc);
          if (slot != -1) {
            ItemStack stackInSlot = te.records.getStackInSlot(slot);
            if (!stackInSlot.isEmpty()) {
              str = ((MusicDiscItem)stackInSlot.getItem()).getRecordDescription().getFormattedText();
              GameRenderer.drawNameplate(this.getFontRenderer(), str, (float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F, 0, f, f1, false);
            }
          }
        }
      }
    }
  }
}
