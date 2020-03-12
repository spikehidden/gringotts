package org.gestern.gringotts.dependency.towny;


import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;

import java.util.UUID;

/**
 * The type Valid towny handler.
 */
class ValidTownyHandler extends TownyHandler implements AccountHolderProvider {
    private static final String TAG_TOWN = "town";
    private static final String TAG_NATION = "nation";
    private final Towny plugin;

    /**
     * Instantiates a new Valid towny handler.
     *
     * @param plugin the plugin
     */
    public ValidTownyHandler(Towny plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new TownyListener(), Gringotts.getInstance());
        Gringotts.getInstance().registerAccountHolderProvider(TAG_TOWN, this);
        Gringotts.getInstance().registerAccountHolderProvider(TAG_NATION, this);
    }

    /**
     * Get a TownyAccountHolder for the town of which player is a resident, if any.
     *
     * @param player player to get town for
     * @return TownyAccountHolder for the town of which player is a resident, if any. null otherwise.
     */
    @Override
    public TownyAccountHolder getTownAccountHolder(Player player) {
        try {
            Resident resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
            Town town = resident.getTown();

            return new TownyAccountHolder(town, TAG_TOWN);
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
    public TownyAccountHolder getNationAccountHolder(Player player) {
        try {
            Resident resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
            Town town = resident.getTown();
            Nation nation = town.getNation();

            return new TownyAccountHolder(nation, TAG_NATION);
        } catch (NotRegisteredException ignored) {
        }

        return null;
    }

    /**
     * Get a TownyAccountHolder based on the name of the account.
     * Names beginning with "town-" will beget a town account holder and names beginning with "nation-"
     * a nation account holder.
     *
     * @param name Name of the account.
     * @return a TownyAccountHolder based on the name of the account
     */
    @Override
    public TownyAccountHolder getAccountHolderByAccountName(String name) {
        if (name.startsWith("town-")) {
            try {
                Town teo = TownyAPI.getInstance().getDataSource().getTown(name.substring(5));

                return new TownyAccountHolder(teo, TAG_TOWN);
            } catch (NotRegisteredException ignored) {
            }
        }

        if (name.startsWith("nation-")) {
            try {
                Nation teo = TownyAPI.getInstance().getDataSource().getNation(name.substring(7));

                return new TownyAccountHolder(teo, TAG_NATION);
            } catch (NotRegisteredException ignored) {
            }
        }

        return null;
    }


    /**
     * Enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isEnabled() {
        return plugin != null;
    }

    /**
     * Exists boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isPresent() {
        return plugin != null;
    }

    /**
     * Gets account holder.
     *
     * @param id the id
     * @return the account holder
     */
    @Override
    public TownyAccountHolder getAccountHolder(String id) {
        return getAccountHolderByAccountName(id);
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param uuid the uuid
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
}
