package io.github.betterclient.crosshairutils;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrosshairUtils implements ClientModInitializer {
    private static final Logger  LOGGER = LoggerFactory.getLogger("CrosshairUtils");

    @Override
    public void onInitializeClient() {
        LOGGER.info("CrosshairUtils initialized");
    }
}
