package org.gestern.gringotts.dependency.towny.nation;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.RenameNationEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyObject;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.AccountChest;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.event.VaultCreationEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The type Nation holder provider.
 */
public class NationHolderProvider implements AccountHolderProvider, Listener {
    private final Gringotts gringotts;

    /**
     * Instantiates a new Nation holder provider.
     *
     * @param gringotts the gringotts
     */
    public NationHolderProvider(@NotNull Gringotts gringotts) {
        this.gringotts = gringotts;
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param id id of account holder
     * @return account holder for id
     */
    @Override
    public AccountHolder getAccountHolder(@NotNull String id) {
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
    public AccountHolder getAccountHolder(@NotNull UUID uuid) {
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
    public AccountHolder getAccountHolder(@NotNull OfflinePlayer player) {
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
     * Gets type.
     *
     * @return the type
     */
    @Override
    public VaultCreationEvent.Type getType() {
        return VaultCreationEvent.Type.NATION;
    }

    /**
     * Gets account names.
     *
     * @return the account names
     */
    @Override
    public Set<String> getAccountNames() {
        return TownyAPI.getInstance()
                .getDataSource()
                .getNations()
                .stream()
                .map(TownyObject::getName)
                .collect(Collectors.toSet());
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
        Nation nation = event.getNation();

        AccountHolder holder = this.getAccountHolder(nation);

        GringottsAccount account = this.gringotts.getAccounting().getAccount(holder);

        this.gringotts.getDao().retrieveChests(account).forEach(AccountChest::updateSign);
    }
}
