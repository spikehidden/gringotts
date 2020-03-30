package org.gestern.gringotts.dependency.towny.nation;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.RenameNationEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
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
 * The type Nation holder provider.
 */
public class NationHolderProvider implements AccountHolderProvider, Listener {
    private final Gringotts gringotts;
    private final TownyHandler handler;

    /**
     * Instantiates a new Nation holder provider.
     *
     * @param handler   the handler
     * @param gringotts the gringotts
     */
    public NationHolderProvider(TownyHandler handler, Gringotts gringotts) {
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
            if (id.startsWith(VaultCreationEvent.Type.NATION.getId() + "-")) {
                try {
                    return getAccountHolder(
                            TownyAPI.getInstance()
                                    .getDataSource()
                                    .getNation(id.substring(7))
                    );
                } catch (NotRegisteredException ignored1) {
                }
            } else {
                try {
                    return getAccountHolder(TownyAPI.getInstance().getDataSource().getNation(id));
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
            return getAccountHolder(TownyAPI.getInstance().getDataSource().getNation(uuid));
        } catch (NotRegisteredException ignored) {
        }

        return null;
    }

    /**
     * Get a TownyAccountHolder for the nation of which player is a resident, if any.
     *
     * @param player player to get nation for
     * @return TownyAccountHolder for the nation of which player is a resident, if any. null otherwise.
     */
    @Override
    public AccountHolder getAccountHolder(OfflinePlayer player) {
        try {
            Resident resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
            Town town = resident.getTown();
            Nation nation = town.getNation();

            return getAccountHolder(nation);
        } catch (NotRegisteredException ignored) {
        }

        return null;
    }

    /**
     * Gets account holder.
     *
     * @param nation the nation
     * @return the account holder
     */
    public AccountHolder getAccountHolder(Nation nation) {
        return new NationAccountHolder(nation);
    }

    /**
     * Rename nation.
     *
     * @param event the event
     */
    @EventHandler
    public void renameNation(RenameNationEvent event) {
        if (!this.handler.isEnabled()) {
            return;
        }

        Nation nation = event.getNation();

        AccountHolder holder = this.getAccountHolder(nation);

        GringottsAccount account = this.gringotts.getAccounting().getAccount(holder);

        this.gringotts.getDao().retrieveChests(account).forEach(AccountChest::updateSign);
    }
}
