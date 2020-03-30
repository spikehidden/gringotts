package org.gestern.gringotts.dependency.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.*;

/**
 * The type Towny listener.
 */
class TownyListener implements Listener {
    private final ValidTownyHandler handler;

    /**
     * Instantiates a new Towny listener.
     *
     * @param handler the handler
     */
    public TownyListener(ValidTownyHandler handler) {
        this.handler = handler;
    }

    /**
     * Vault created.
     *
     * @param event the event
     */
    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid() || !this.handler.isEnabled()) {
            return;
        }

        String ownerName = event.getCause().getLine(2);
        Player player = event.getCause().getPlayer();
        boolean forOther = ownerName != null && ownerName.length() > 0 && CREATE_VAULT_ADMIN.isAllowed(player);

        AccountHolder owner;

        switch (event.getType()) {
            case TOWN:
                if (!CREATE_VAULT_TOWN.isAllowed(player)) {
                    player.sendMessage(LANG.plugin_towny_noTownVaultPerm);

                    return;
                }

                if (forOther) {
                    try {
                        owner = this.handler.getTownHolderProvider().getAccountHolder(
                                TownyAPI.getInstance().getDataSource().getTown(ownerName)
                        );
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    if (owner == null) {
                        return;
                    }
                } else {
                    owner = this.handler.getTownHolderProvider().getAccountHolder(player);
                }

                if (owner == null) {
                    player.sendMessage(LANG.plugin_towny_noTownResident);

                    return;
                }

                event.setOwner(owner);
                event.setValid(true);

                break;
            case NATION:
                if (!CREATE_VAULT_NATION.isAllowed(player)) {
                    player.sendMessage(LANG.plugin_towny_noNationVaultPerm);

                    return;
                }

                if (forOther) {
                    try {
                        owner = this.handler.getNationHolderProvider().getAccountHolder(
                                TownyAPI.getInstance().getDataSource().getNation(ownerName)
                        );
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    if (owner == null) {
                        return;
                    }
                } else {
                    owner = this.handler.getNationHolderProvider().getAccountHolder(player);
                }

                if (owner == null) {
                    player.sendMessage(LANG.plugin_towny_notInNation);

                    return;
                }

                event.setOwner(owner);
                event.setValid(true);

                break;
        }
    }
}
