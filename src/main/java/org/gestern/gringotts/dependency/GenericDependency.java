package org.gestern.gringotts.dependency;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.api.dependency.Dependency;

/**
 * The type Generic dependency.
 */
public class GenericDependency implements Dependency {
    private final Plugin plugin;
    private final String id;

    /**
     * Instantiates a new Generic dependency.
     *
     * @param plugin the plugin
     * @param id     the id
     */
    public GenericDependency(Plugin plugin,
                             String id) {
        if (plugin == null) {
            throw new NullArgumentException(
                    "The 'plugin' is null"
            );
        }

        this.plugin = plugin;
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Gets plugin.
     *
     * @return the plugin
     */
    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
