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

import static net.minecraft.world.RedstoneView.DIRECTIONS;

@Mixin(ChunkBlockLightProvider.class)
public class ChunkBlockLightProviderMixin {

    @Inject(
            method = "method_51529",
            at = @At("HEAD"),
            cancellable = true
    )
    private void propagateBlockLightOnBlocksPlacedRemoved(long sourceBlockPos, CallbackInfo ci) {

        long field_44731 = ((ChunkLightProviderAccessor) this).getField_44731();
        LightStorage<?> lightStorage = ((ChunkLightProviderAccessor) this).getLightStorage();
        BlockPos.Mutable mutablePos = ((ChunkBlockLightProviderAccessor) this).getMutablePos();

        long sourceSectionPos = ChunkSectionPos.fromBlockPos(sourceBlockPos);
        if (!((LightStorageInvoker) lightStorage).invokerHasSection(sourceSectionPos)) {
            return;
        }
        int sourceLightLevel= ((LightStorageInvoker) lightStorage).invokerGet(sourceBlockPos);
        BlockState blockState = ((ChunkLightProviderInvoker) this).invokerGetStateForLighting(mutablePos.set(sourceBlockPos));
        int lightEmitterLuminance = ((ChunkBlockLightProviderInvoker) this).invokerGetLightSourceLuminance(sourceBlockPos, blockState);
        if (lightEmitterLuminance < sourceLightLevel ) {
            ((LightStorageInvoker) lightStorage).invokerSet(sourceBlockPos, 0);
            ((ChunkLightProviderInvoker) this).invokerQueueForLightDarkening(
                    sourceBlockPos, ChunkLightProvider.class_8531.packWithAllDirectionsSet(sourceLightLevel));
        } else {
            ((ChunkLightProviderInvoker) this).invokerQueueForLightDarkening(sourceBlockPos, field_44731);
        }
        if (lightEmitterLuminance > 0) {
            ((ChunkLightProviderInvoker) this).invokerQueueForLightEnlightening(sourceBlockPos, ChunkLightProvider.class_8531.method_51573(
                    lightEmitterLuminance,
                    ChunkLightProviderInvoker.invokeIsTrivialForLighting(blockState)));
        }
        ci.cancel();
    }

    @Inject(
            method = "method_51531",
            at = @At("HEAD"),
            cancellable = true
    )
    private void propagateBlockLightOnSourcePlaced(long sourceBlockPos, long packedData, int sourceLightLevel, CallbackInfo ci) {

        LightStorage<?> lightStorage = ((ChunkLightProviderAccessor) this).getLightStorage();
        BlockPos.Mutable mutablePos = ((ChunkBlockLightProviderAccessor) this).getMutablePos();

        BlockState sourceBlockState = ChunkLightProvider.class_8531.isTrivial(packedData) ?
                Blocks.AIR.getDefaultState()
                :
                ((ChunkLightProviderInvoker) this).invokerGetStateForLighting(mutablePos.set(sourceBlockPos));
//        int decay = 0;

//        for (Direction direction : DIRECTIONS) {
//            long blockPos = BlockPos.offset(sourceBlockPos, direction);
//            mutablePos.set(blockPos);
//            BlockState targetBlockState = ((ChunkLightProviderInvoker) this).invokerGetStateForLighting(mutablePos);
//            int targetOpacity = ((ChunkLightProviderInvoker) this).invokerGetOpacity(targetBlockState, mutablePos);
//            if (targetOpacity > 1) decay += 2;
//        }
//        if (decay > 2) decay = 2;
//        sourceLightLevel -= decay ; // Decay light if source is occluded

        for (Direction direction : DIRECTIONS) {
            long targetBlockPos = BlockPos.offset(sourceBlockPos, direction);
            int targetLightLevel_Old = ((LightStorageInvoker) lightStorage).invokerGet(targetBlockPos);

            // Can only propagate to darker, existing areas.
            if (
                    !ChunkLightProvider.class_8531.isDirectionBitSet(packedData, direction)
                            || !((LightStorageInvoker) lightStorage).invokerHasSection(ChunkSectionPos.fromBlockPos(targetBlockPos))
                            || (sourceLightLevel - 1) <= (targetLightLevel_Old)
            ) continue;
            mutablePos.set(targetBlockPos);
            BlockState targetBlockState = ((ChunkLightProviderInvoker) this).invokerGetStateForLighting(mutablePos);
            int targetOpacity = ((ChunkLightProviderInvoker) this).invokerGetOpacity(targetBlockState, mutablePos);
            int targetLightLevel_New =
                    sourceLightLevel - targetOpacity;

            // Propagated light must be brighter.
            if (targetLightLevel_New <= targetLightLevel_Old) continue;

//            if (targetLightLevel_New < 10) {
//                int sum =
//                        BlockPos.unpackLongX(targetBlockPos)
//                                + BlockPos.unpackLongY(targetBlockPos)
//                                + BlockPos.unpackLongZ(targetBlockPos);
//                if (sum % 2 == 0 && targetOpacity == 1) targetLightLevel_New = sourceLightLevel;
//            }

            if (((ChunkLightProviderInvoker) this).invokerShapesCoverFullCube(
                    sourceBlockPos, sourceBlockState, targetBlockPos, targetBlockState, direction)) continue;

            ((LightStorageInvoker) lightStorage).invokerSet(targetBlockPos, targetLightLevel_New);

            if (targetLightLevel_New <= 1) continue;

            // Queue next propagation
            ((ChunkLightProviderInvoker) this).invokerQueueForLightEnlightening(
                    targetBlockPos,
                    ChunkLightProvider.class_8531.method_51574(
                            targetLightLevel_New,
                            ChunkLightProviderInvoker.invokeIsTrivialForLighting(targetBlockState),
                            direction.getOpposite()
                    )
            );
        }
        ci.cancel();
    }

    @Inject(
            method = "method_51530",
            at = @At("HEAD"),
            cancellable = true
    )
    private void propagateBlockLightOnSourceRemoved(long sourceBlockPos, long packedData, CallbackInfo ci) {
        LightStorage<?> lightStorage = ((ChunkLightProviderAccessor) this).getLightStorage();
        BlockPos.Mutable mutablePos = ((ChunkBlockLightProviderAccessor) this).getMutablePos();

        int sourceLightLevel = ChunkLightProvider.class_8531.getLightLevel(packedData);
        for (Direction direction : DIRECTIONS) {
            int targetLightLevel_Old;
            long targetBlockPos = BlockPos.offset(sourceBlockPos, direction);
            if (
                    !ChunkLightProvider.class_8531.isDirectionBitSet(packedData, direction)
                            || !((LightStorageInvoker) lightStorage).invokerHasSection(
                                    ChunkSectionPos.fromBlockPos(targetBlockPos))
                            || (targetLightLevel_Old = ((LightStorageInvoker) lightStorage).invokerGet(targetBlockPos)) == 0
            ) continue;

            if (targetLightLevel_Old <= sourceLightLevel - 1) {
                BlockState blockState = ((ChunkLightProviderInvoker) this).invokerGetStateForLighting(mutablePos.set(targetBlockPos));
                int lightEmitterLuminance = ((ChunkBlockLightProviderInvoker) this).invokerGetLightSourceLuminance(targetBlockPos, blockState);
                ((LightStorageInvoker) lightStorage).invokerSet(targetBlockPos, 0);
                if (lightEmitterLuminance < targetLightLevel_Old) {
                    ((ChunkLightProviderInvoker) this).invokerQueueForLightDarkening(targetBlockPos, ChunkLightProvider.class_8531.packWithOneDirectionCleared(targetLightLevel_Old, direction.getOpposite()));
                }

                if (lightEmitterLuminance <= 0) continue;

                ((ChunkLightProviderInvoker) this).invokerQueueForLightEnlightening(
                        targetBlockPos, ChunkLightProvider.class_8531.method_51573(
                                lightEmitterLuminance, ChunkLightProviderInvoker.invokeIsTrivialForLighting(blockState)
                        )
                );
                continue;
            }
            ((ChunkLightProviderInvoker) this).invokerQueueForLightEnlightening(
                    targetBlockPos, ChunkLightProvider.class_8531.method_51579(
                            targetLightLevel_Old, false, direction.getOpposite()
                    )
            );
        }
        ci.cancel();
    }

}
