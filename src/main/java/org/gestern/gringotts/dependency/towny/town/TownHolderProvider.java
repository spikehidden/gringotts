package org.gestern.gringotts.dependency.towny.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.AccountChest;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.dependency.towny.TownyHandler;
import org.gestern.gringotts.event.VaultCreationEvent;

import java.util.UUID;

/**
 * The type Town holder provider.
 */
public class TownHolderProvider implements AccountHolderProvider, Listener {
    private final Gringotts gringotts;
    private final TownyHandler handler;

    /**
     * Instantiates a new Town holder provider.
     *
     * @param handler   the handler
     * @param gringotts the gringotts
     */
    public TownHolderProvider(TownyHandler handler, Gringotts gringotts) {
        this.handler = handler;
        this.gringotts = gringotts;

        Bukkit.getPluginManager().registerEvents(this, this.gringotts);
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param id id of account holder
     * @return account holder for id
     */
    @Override
    public AccountHolder getAccountHolder(String id) {
        try {
            return getAccountHolder(UUID.fromString(id));
        } catch (IllegalArgumentException ignored) {
            if (id.startsWith(VaultCreationEvent.Type.TOWN.getId() + "-")) {
                try {
                    return getAccountHolder(
                            TownyAPI.getInstance()
                                    .getDataSource()
                                    .getTown(id.substring(5))
                    );
                } catch (NotRegisteredException ignored1) {
                }
            } else {
                try {
                    return getAccountHolder(TownyAPI.getInstance().getDataSource().getTown(id));
                } catch (NotRegisteredException ignored1) {
                }
            }
        }

        return null;
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param uuid id of account holder
     * @return account holder for id
     */
    @Override
    public AccountHolder getAccountHolder(UUID uuid) {
        try {
            return getAccountHolder(TownyAPI.getInstance().getDataSource().getTown(uuid));
        } catch (NotRegisteredException ignored) {
        }

        return null;
    }

    /**
     * Get a TownyAccountHolder for the town of which player is a resident, if any.
     *
     * @param player player to get town for
     * @return TownyAccountHolder for the town of which player is a resident, if any. null otherwise.
     */
    @Override
    public AccountHolder getAccountHolder(OfflinePlayer player) {
        try {
            Resident resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
            Town town = resident.getTown();

            return getAccountHolder(town);
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Gets account holder.
     *
     * @param town the town
     * @return the account holder
     */
    public AccountHolder getAccountHolder(Town town) {
        return new TownAccountHolder(town);
    }

    /**
     * Rename town.
     *
     * @param event the event
     */
    @EventHandler
    public void renameTown(RenameTownEvent event) {
        if (!this.handler.isEnabled()) {
            return;
        }

        Town town = event.getTown();

        AccountHolder holder = this.getAccountHolder(town);

        GringottsAccount account = this.gringotts.getAccounting().getAccount(holder);

        this.gringotts.getDao().retrieveChests(account).forEach(AccountChest::updateSign);
    }
}
