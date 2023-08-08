package aitirava.furtherlights.server.mixins.world.chunk.light;

import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkLightProvider.class)
public interface ChunkLightProviderAccessor {
    @Accessor
    @Mutable
    LightStorage<?> getLightStorage();

}
