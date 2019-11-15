package com.tfar.discholder;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

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
          if (item.isEmpty())continue;
          //double blockScale = 1;
          shiftX = isXAxis ? .5 - .03125 : .125 + .125 * i;
          shiftY = .375;
          shiftZ = isXAxis ? .125 + .125 * i : .5 + .03125;
          GlStateManager.pushMatrix();
          GlStateManager.enableBlend();
          GlStateManager.translated(x + shiftX, y + shiftY, z + shiftZ);
          if (!isXAxis)
          GlStateManager.rotated(90,0,1,0);
        Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.FIXED);
          GlStateManager.popMatrix();
      }
    }
  }
}
