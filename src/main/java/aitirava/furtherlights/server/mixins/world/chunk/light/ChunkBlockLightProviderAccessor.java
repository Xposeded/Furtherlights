package aitirava.furtherlights.server.mixins.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkBlockLightProvider.class)
public interface ChunkBlockLightProviderAccessor {


    @Accessor @Mutable
     BlockPos.Mutable getMutablePos();




}
