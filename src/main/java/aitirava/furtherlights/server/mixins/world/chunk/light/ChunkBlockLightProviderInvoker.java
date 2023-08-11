package aitirava.furtherlights.server.mixins.world.chunk.light;

import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkBlockLightProvider.class)
public interface ChunkBlockLightProviderInvoker {

    @Invoker("getLightSourceLuminance")
    int invokerGetLightSourceLuminance(long blockPos, BlockState blockState);


}
