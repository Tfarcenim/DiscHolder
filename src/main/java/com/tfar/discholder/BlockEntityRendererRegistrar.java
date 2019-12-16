package com.tfar.discholder;

import net.minecraftforge.fml.client.registry.ClientRegistry;

class BlockEntityRendererRegistrar {
    static void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(DiscHolderBlockEntity.class, new DiscHolderBlockEntityRenderer());
    }
}
