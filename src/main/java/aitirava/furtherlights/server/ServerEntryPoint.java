package aitirava.furtherlights.server;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerEntryPoint implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("FurtherLights");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("***FurtherLights initialized in server!***" +
                (1 + MathHelper.floorLog2(MathHelper.smallestEncompassingPowerOfTwo(30000000)) )
        );
    }
}