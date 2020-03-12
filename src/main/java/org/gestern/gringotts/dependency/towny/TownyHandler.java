package org.gestern.gringotts.dependency.towny;

import com.palmergames.bukkit.towny.Towny;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.dependency.DependencyHandler;

/**
 * The type Towny handler.
 */
public abstract class TownyHandler implements DependencyHandler {
    /**
     * Get a valid towny handler if the plugin instance is valid. Otherwise get a fake one.
     * Apparently Towny needs this special treatment, or it will throw exceptions with unavailable classes.
     *
     * @param towny Towny plugin instance
     * @return a Towny handler
     */
    public static TownyHandler getTownyHandler(Plugin towny) {
        if (towny instanceof Towny) {
            return new ValidTownyHandler((Towny) towny);
        } else {
            Gringotts.getInstance().getLogger().warning(
                    "Unable to load Towny handler. Towny support will not work"
            );

            return new InvalidTownyHandler();
        }
    }

    /**
     * Gets town account holder.
     *
     * @param player the player
     * @return the town account holder
     */
    public abstract TownyAccountHolder getTownAccountHolder(Player player);

    /**
     * Gets nation account holder.
     *
     * @param player the player
     * @return the nation account holder
     */
    public abstract TownyAccountHolder getNationAccountHolder(Player player);

    /**
     * Gets account holder by account name.
     *
     * @param name the name
     * @return the account holder by account name
     */
    public abstract TownyAccountHolder getAccountHolderByAccountName(String name);
}
