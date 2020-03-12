package org.gestern.gringotts.dependency.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.dependency.DependencyHandler;

/**
 * The type World guard handler.
 */
public abstract class WorldGuardHandler implements DependencyHandler, AccountHolderProvider {
    /**
     * Gets world guard handler.
     *
     * @param plugin the plugin
     * @return the world guard handler
     */
    public static WorldGuardHandler getWorldGuardHandler(Plugin plugin) {
        if (plugin instanceof WorldGuardPlugin) {
            return new ValidWorldGuardHandler((WorldGuardPlugin) plugin);
        } else {
            return new InvalidWorldGuardHandler();
        }
    }
}
