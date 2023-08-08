package aitirava.furtherlights.server.mixins.world.chunk.light;


import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkLightProvider.class)
public class ChunkLightProviderMixin {

    @Inject(
            method = "getOpacity",
            at = @At("RETURN")
    )
    private void injected(BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {

    }


}
