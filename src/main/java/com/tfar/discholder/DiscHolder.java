package com.tfar.discholder;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DiscHolder.MODID)
public class DiscHolder
{
  // Directly reference a log4j logger.

  public static final String MODID = "discholder";

  private static final Logger LOGGER = LogManager.getLogger();

  public DiscHolder() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the doClientStuff method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

  private void setup(final FMLCommonSetupEvent event) {
  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    BlockEntityRendererRegistrar.registerRenderers();
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    private static final Set<Block> discholders = new HashSet<>();

    @SubscribeEvent
    public static void block(final RegistryEvent.Register<Block> e) {
      Arrays.stream(DyeColor.values()).forEach(dyeColor -> register(new DiscHolderBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD))
              ,dyeColor.getTranslationKey() + "_discholder",e.getRegistry()));
    }

    @SubscribeEvent
    public static void item(final RegistryEvent.Register<Item> e) {
      discholders.forEach(block -> e.getRegistry().register(new BlockItem(block,new Item.Properties().group(ItemGroup.TOOLS)).setRegistryName(block.getRegistryName())));
    }

    @SubscribeEvent
    public static void tile(final RegistryEvent.Register<TileEntityType<?>> e) {
      e.getRegistry().register(TileEntityType.Builder.create(DiscHolderBlockEntity::new, discholders.toArray(new Block[0])).build(null).setRegistryName("discholder"));
    }

    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
      if (obj instanceof Block) discholders.add((Block) obj);
    }

  }

  public static class Objects {

    @ObjectHolder(MODID)
    public static class Tiles {
      public static final TileEntityType<?> discholder = null;
    }
  }
}
