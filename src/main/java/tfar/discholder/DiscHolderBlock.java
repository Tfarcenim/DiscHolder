package tfar.discholder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class DiscHolderBlock extends Block {

  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

  public DiscHolderBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  public static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 6, 16);


  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if (!world.isRemote) {
      Vector3d vec3d = hit.getHitVec();
      Direction facing = state.get(HorizontalBlock.HORIZONTAL_FACING);
      double inc = (facing == Direction.NORTH || facing == Direction.SOUTH) ? vec3d.x % 1 : vec3d.z % 1;
      int slot = getSlot(vec3d, facing);
      if (slot != -1) {
        ItemStack heldItem = player.getHeldItem(handIn);
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof DiscHolderBlockEntity) {
          DiscHolderBlockEntity discHolder = (DiscHolderBlockEntity) blockEntity;
          if (heldItem.getItem() instanceof MusicDiscItem && discHolder.records.getStackInSlot(slot).isEmpty()) {
            discHolder.records.setStackInSlot(slot, heldItem.copy());
            if (!player.abilities.isCreativeMode)
            player.getHeldItem(handIn).shrink(1);
          } else if (heldItem.isEmpty() && !discHolder.records.getStackInSlot(slot).isEmpty()) {
            ItemStack record = discHolder.records.extractItem(slot, 64, false);
            world.addEntity(new ItemEntity(world, player.getPosX(), pos.getY() + .5, player.getPosZ(), record));
          }
        }
      }
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof DiscHolderBlockEntity) {
        dropItems(((DiscHolderBlockEntity) tileentity).records, worldIn, pos);
      }
      worldIn.updateComparatorOutputLevel(pos, this);
    }
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }


  public static void dropItems(IItemHandler inv, World world, BlockPos pos) {
    IntStream.range(0, inv.getSlots()).mapToObj(inv::getStackInSlot).filter(s -> !s.isEmpty()).forEach(stack -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   * @deprecated call via {@link BlockState#rotate(Rotation)} (Rotation)} whenever possible. Implementing/overriding is
   * fine.
   */
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   * @deprecated call via {@link BlockState#mirror(Mirror)} (Mirror)} whenever possible. Implementing/overriding is fine.
   */
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get(FACING)));
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  public static int getSlot(Vector3d inc, Direction facing) {
    return 0;//inc < 1 / 16d || inc > 15 / 16d ? -1 : (int) (8 * inc - .5);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new DiscHolderBlockEntity();
  }
}
