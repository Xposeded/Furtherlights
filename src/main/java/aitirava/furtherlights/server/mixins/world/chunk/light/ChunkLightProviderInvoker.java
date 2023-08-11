package aitirava.furtherlights.server.mixins.world.chunk.light;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkLightProvider.class)
public interface ChunkLightProviderInvoker {
    @Invoker("getStateForLighting")@Mutable
    BlockState invokerGetStateForLighting(BlockPos pos);

    @Invoker("method_51565")
    void invokerQueueForLightDarkening(long blockPos, long flags);

    @Invoker("method_51566")
    void invokerQueueForLightEnlightening(long blockPos, long flags);

    @Invoker("isTrivialForLighting")
    static boolean invokeIsTrivialForLighting(BlockState blockState) {
        throw new AssertionError();
    }

    @Invoker("shapesCoverFullCube")
    boolean invokerShapesCoverFullCube(long sourceId, BlockState sourceState, long targetId, BlockState targetState, Direction direction);

    @Invoker("getOpacity")
    int invokerGetOpacity(BlockState state, BlockPos pos);
}
