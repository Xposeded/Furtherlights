package aitirava.furtherlights.server.mixins.world.chunk.light;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;

import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.Math.random;
import static net.minecraft.world.RedstoneView.DIRECTIONS;

@Mixin(ChunkBlockLightProvider.class)
public class ChunkBlockLightProviderMixin {

    @Inject(
            method = "method_51531",
            at = @At("HEAD"),
            cancellable = true
    )
    private void propagateBlockLight(long chunkPos, long packedData, int sourceLightLevel, CallbackInfo ci) {

        LightStorage<?> lightStorage = ((ChunkLightProviderAccessor)this).getLightStorage();
        BlockPos.Mutable mutablePos = ((ChunkBlockLightProviderAccessor)this).getMutablePos();

        BlockState sourceBlockState = ChunkLightProvider.class_8531.isTrivial(packedData) ?
                Blocks.AIR.getDefaultState()
                :
                ((ChunkLightProviderInvoker) this).invokerGetStateForLighting(mutablePos.set(chunkPos));

        for (Direction direction : DIRECTIONS) {
            long blockPos = BlockPos.offset(chunkPos, direction);
            int targetLightLevel_Old = ((LightStorageInvoker)lightStorage).invokerGet(blockPos);

            // Can only propagate to darker, existing areas.
            if (!ChunkLightProvider.class_8531.isDirectionBitSet(packedData, direction)
                    || !((LightStorageInvoker)lightStorage).invokerHasSection(ChunkSectionPos.fromBlockPos(blockPos))
                    || (sourceLightLevel - 1) <= (targetLightLevel_Old)
            ) continue;
            mutablePos.set(blockPos);
            BlockState targetBlockState = ((ChunkLightProviderInvoker)this).invokerGetStateForLighting(mutablePos);
            int targetLightLevel_New =
                    sourceLightLevel - ((ChunkLightProviderInvoker)this).invokerGetOpacity(targetBlockState, mutablePos);
            if(random() < 0.5) targetLightLevel_New = targetLightLevel_Old; // Randomized light
            // Propagated light must be brighter.
            if (targetLightLevel_New <= targetLightLevel_Old) continue;

            if (((ChunkLightProviderInvoker)this).invokerShapesCoverFullCube(
                    chunkPos, sourceBlockState, blockPos, targetBlockState, direction)) continue;
            ((LightStorageInvoker) lightStorage).invokerSet(blockPos, targetLightLevel_New);

            if (targetLightLevel_New <= 1) continue;

            // Queue next propagation
            ((ChunkLightProviderInvoker)this).invokerMethod_51566(
                    blockPos,
                    ChunkLightProvider.class_8531.method_51574(
                            targetLightLevel_New,
                            ChunkLightProviderInvoker.invokeIsTrivialForLighting(targetBlockState),
                            direction.getOpposite()
                    )
            );
        }
        ci.cancel();
    }
}
