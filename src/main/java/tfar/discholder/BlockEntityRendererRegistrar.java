package tfar.discholder;

import net.minecraftforge.fml.client.registry.ClientRegistry;

class BlockEntityRendererRegistrar {
    static void registerRenderers() {
        ClientRegistry.bindTileEntityRenderer(DiscHolder.discholder, DiscHolderBlockEntityRenderer::new);
    }
}
