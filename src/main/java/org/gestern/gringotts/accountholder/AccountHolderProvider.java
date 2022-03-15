package org.gestern.gringotts.accountholder;


import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.event.VaultCreationEvent;

import java.util.Set;
import java.util.UUID;

/**
 * Provides AccountHolder objects for a given id.
 * An AccountHolderProvider has its own internal mapping of ids to account holders.
 * For example, a Factions provider would return a FactionAccountHolder object when given the faction's id.
 *
 * @author jast
 */
public interface AccountHolderProvider {

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param id id of account holder
     * @return account holder for id
     */
    AccountHolder getAccountHolder(String id);

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param uuid id of account holder
     * @return account holder for id
     */
    AccountHolder getAccountHolder(UUID uuid);

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param player the target player
     * @return account holder for id
     */
    AccountHolder getAccountHolder(OfflinePlayer player);

    /**
     * Gets type.
     *
     * @return the type
     */
    VaultCreationEvent.Type getType();

    /**
     * Gets account names.
     *
     * @return the account names
     */
    Set<String> getAccountNames();
}
