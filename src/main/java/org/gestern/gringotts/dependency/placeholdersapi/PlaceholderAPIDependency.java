package org.gestern.gringotts.dependency.placeholdersapi;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.api.dependency.Dependency;
import org.gestern.gringotts.dependency.placeholdersapi.placeholders.PlaceholdersRegister;

public class PlaceholderAPIDependency implements Dependency, Listener {
    private final PlaceholderAPIPlugin plugin;
    private final String               id;

    /**
     * Instantiates a new PlaceholderAPI dependency.
     *
     * @param plugin the plugin
     */
    public PlaceholderAPIDependency(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("'plugin' is null");
        }

        if (!(plugin instanceof PlaceholderAPIPlugin)) {
            throw new IllegalArgumentException(
                    "The 'plugin' needs to be an instance of me.clip.placeholderapi.PlaceholderAPIPlugin"
            );
        }

        this.plugin = (PlaceholderAPIPlugin) plugin;
        this.id     = "placeholderapi";
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
        return this.plugin;
    }

    @Override
    public void onEnable() {
        new PlaceholdersRegister(Gringotts.instance).register();
    }
}
