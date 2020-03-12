package org.gestern.gringotts.dependency.towny;

import org.bukkit.entity.Player;

/**
 * Dummy implementation of towny handler, if the plugin cannot be loaded.
 *
 * @author jast
 */
class InvalidTownyHandler extends TownyHandler {

    /**
     * Enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * Exists boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isPresent() {
        return false;
    }

    /**
     * Gets town account holder.
     *
     * @param player the player
     * @return the town account holder
     */
    @Override
    public TownyAccountHolder getTownAccountHolder(Player player) {
        return null;
    }

    /**
     * Gets nation account holder.
     *
     * @param player the player
     * @return the nation account holder
     */
    @Override
    public TownyAccountHolder getNationAccountHolder(Player player) {
        return null;
    }

    /**
     * Gets account holder by account name.
     *
     * @param name the name
     * @return the account holder by account name
     */
    @Override
    public TownyAccountHolder getAccountHolderByAccountName(String name) {
        return null;
    }

}