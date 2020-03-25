package com.oglofus.gringotts;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "gringotts",
        name = "Gringotts",
        version = "3.0.0-SNAPSHOT",
        description = "Example"
)
public class GringottsSponge {
    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Successfully running Gringotts!!!");
    }
}
