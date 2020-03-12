package org.gestern.gringotts.dependency.towny;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;
import org.gestern.gringotts.event.VaultCreationEvent;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.*;
import static org.gestern.gringotts.dependency.Dependency.DEP;

/**
 * The type Towny listener.
 */
class TownyListener implements Listener {

    /**
     * Vault created.
     *
     * @param event the event
     */
    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) {
            return;
        }

        if (!DEP.towny.isEnabled()) {
            return;
        }

        String ownerName = event.getCause().getLine(2);
        Player player = event.getCause().getPlayer();
        boolean forOther = ownerName != null && ownerName.length() > 0 && CREATE_VAULT_ADMIN.isAllowed(player);

        AccountHolder owner;

        if (event.getType() == VaultCreationEvent.Type.TOWN) {
            if (!CREATE_VAULT_TOWN.isAllowed(player)) {
                player.sendMessage(LANG.plugin_towny_noTownVaultPerm);

                return;
            }

            if (forOther) {
                owner = DEP.towny.getAccountHolderByAccountName("town-" + ownerName);

                if (owner == null) {
                    return;
                }
            } else {
                owner = DEP.towny.getTownAccountHolder(player);
            }

            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_noTownResident);

                return;
            }

            event.setOwner(owner);
            event.setValid(true);
        } else if (event.getType() == VaultCreationEvent.Type.NATION) {
            if (!CREATE_VAULT_NATION.isAllowed(player)) {
                player.sendMessage(LANG.plugin_towny_noNationVaultPerm);

                return;
            }

            if (forOther) {
                owner = DEP.towny.getAccountHolderByAccountName("nation-" + ownerName);

                if (owner == null) {
                    return;
                }
            } else {
                owner = DEP.towny.getNationAccountHolder(player);
            }

            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_notInNation);

                return;
            }

            event.setOwner(owner);
            event.setValid(true);
        }
    }
}
