package aitirava.furtherlights.server.mixins.world.chunk.light;

import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightStorage.class)
public interface LightStorageInvoker {

    @Invoker("get")
    int invokerGet(long blockPos);


    @Invoker("set")
    void invokerSet(long blockPos, int value);

    @Invoker("hasSection")
    boolean invokerHasSection(long sectionPos);
}
