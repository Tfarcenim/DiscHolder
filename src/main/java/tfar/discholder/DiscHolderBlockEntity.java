package tfar.discholder;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class DiscHolderBlockEntity extends TileEntity {

  public final ItemStackHandler records = new ItemStackHandler(7){
    @Override
    protected void onContentsChanged(int slot) {
      super.onContentsChanged(slot);
      DiscHolderBlockEntity.this.markDirty();
    }
  };

  public DiscHolderBlockEntity() {
    super(DiscHolder.discholder);
  }

  @Override
  public void read(BlockState state, CompoundNBT tag) {
    CompoundNBT invTag = tag.getCompound("inv");
    records.deserializeNBT(invTag);
    super.read(state,tag);
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    CompoundNBT compound = this.records.serializeNBT();
    tag.put("inv", compound);
    return super.write(tag);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return write(new CompoundNBT());    // okay to send entire inventory on chunk load
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
    this.read(getBlockState(), packet.getNbtCompound());
  }

  public void updateClient(){
    this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 2);
  }

  @Override
  public void markDirty() {
    super.markDirty();
    updateClient();
  }

}
