package org.gestern.gringotts.dependency;

import org.bukkit.plugin.Plugin;

/**
 * The type Generic handler.
 */
public class GenericHandler implements DependencyHandler {
    private final Plugin plugin;

    /**
     * Instantiates a new Generic handler.
     *
     * @param plugin the plugin
     */
    public GenericHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isEnabled() {
        return plugin != null && plugin.isEnabled();
    }

    /**
     * Is present boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isPresent() {
        return plugin != null;
    }
}
