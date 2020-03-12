package org.gestern.gringotts.dependency.worldguard;

import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.accountholder.AccountHolder;

import java.util.UUID;

/**
 * The type Invalid world guard handler.
 */
class InvalidWorldGuardHandler extends WorldGuardHandler {

    /**
     * Gets account holder.
     *
     * @param id the id
     * @return the account holder
     */
    @Override
    public AccountHolder getAccountHolder(String id) {
        return null;
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param uuid the targeted account holder
     * @return account holder for id
     */
    @Override
    public AccountHolder getAccountHolder(UUID uuid) {
        return null;
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param player the target player
     * @return account holder for id
     */
    @Override
    public AccountHolder getAccountHolder(OfflinePlayer player) {
        return null;
    }

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
}