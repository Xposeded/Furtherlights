package aitirava.furtherlights.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEntryPoint implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("FurtherLights");

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
         LOGGER.info("***FurtherLights initialized in client!***");
    }
}