package org.gestern.gringotts.dependency.towny;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.Language;
import org.gestern.gringotts.Permissions;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.api.dependency.Dependency;
import org.gestern.gringotts.dependency.towny.nation.NationHolderProvider;
import org.gestern.gringotts.dependency.towny.town.TownHolderProvider;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;
import org.gestern.gringotts.event.VaultCreationEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type Towny dependency.
 */
public class TownyDependency implements Dependency, Listener {
    private final NationHolderProvider nationHolderProvider;
    private final TownHolderProvider townHolderProvider;
    private final Gringotts gringotts;
    private final Towny plugin;
    private final String id;

    /**
     * Instantiates a new Towny dependency.
     *
     * @param gringotts the gringotts
     * @param plugin    the plugin
     */
    public TownyDependency(@NotNull Gringotts gringotts,
                           @Nullable Plugin plugin) {
        if (plugin == null) {
            throw new NullArgumentException("'plugin' is null");
        }

        if (!(plugin instanceof Towny)) {
            throw new IllegalArgumentException(
                    "The 'plugin' needs to be an instance of com.palmergames.bukkit.towny.Towny"
            );
        }

        this.gringotts = gringotts;
        this.plugin = (Towny) plugin;
        this.id = "towny";

        this.nationHolderProvider = new NationHolderProvider(gringotts);
        this.townHolderProvider = new TownHolderProvider(gringotts);
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

    /**
     * On enable.
     */
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(
                this,
                this.gringotts
        );
        Bukkit.getPluginManager().registerEvents(
                this.townHolderProvider,
                this.gringotts
        );
        Bukkit.getPluginManager().registerEvents(
                this.nationHolderProvider,
                this.gringotts
        );

        Gringotts.getInstance().registerAccountHolderProvider(
                VaultCreationEvent.Type.TOWN.getId(),
                this.townHolderProvider
        );
        Gringotts.getInstance().registerAccountHolderProvider(
                VaultCreationEvent.Type.NATION.getId(),
                this.nationHolderProvider
        );
    }

    /**
     * Vault created.
     *
     * @param event the event
     */
    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid() || !this.isEnabled()) {
            return;
        }

        String line2String = event.getCause().getLine(2);

        if (line2String == null) {
            return;
        }

        Player player = event.getCause().getPlayer();

        boolean forOther = line2String.length() > 0 && Permissions.CREATE_VAULT_ADMIN.isAllowed(player);

        AccountHolder owner;

        switch (event.getType()) {
            case TOWN: {
                if (!Permissions.CREATE_VAULT_TOWN.isAllowed(player)) {
                    player.sendMessage(Language.LANG.plugin_towny_noTownVaultPerm);

                    return;
                }

                if (forOther) {
                    try {
                        owner = this.townHolderProvider.getAccountHolder(
                                TownyAPI.getInstance().getDataSource().getTown(line2String)
                        );
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    if (owner == null) {
                        return;
                    }
                } else {
                    owner = this.townHolderProvider.getAccountHolder(player);
                }

                if (owner == null) {
                    player.sendMessage(Language.LANG.plugin_towny_noTownResident);

                    return;
                }

                event.setOwner(owner);
                event.setValid(true);

                break;
            }
            case NATION: {
                if (!Permissions.CREATE_VAULT_NATION.isAllowed(player)) {
                    player.sendMessage(Language.LANG.plugin_towny_noNationVaultPerm);

                    return;
                }

                if (forOther) {
                    try {
                        owner = this.nationHolderProvider.getAccountHolder(
                                TownyAPI.getInstance().getDataSource().getNation(line2String)
                        );
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    if (owner == null) {
                        return;
                    }
                } else {
                    owner = this.nationHolderProvider.getAccountHolder(player);
                }

                if (owner == null) {
                    player.sendMessage(Language.LANG.plugin_towny_notInNation);

                    return;
                }

                event.setOwner(owner);
                event.setValid(true);

                break;
            }
        }
    }
}
