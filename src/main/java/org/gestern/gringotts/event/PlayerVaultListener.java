package org.gestern.gringotts.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.CREATE_VAULT_ADMIN;
import static org.gestern.gringotts.Permissions.CREATE_VAULT_PLAYER;
import static org.gestern.gringotts.event.VaultCreationEvent.Type;

/**
 * This Vault listener handles vault creation events for player vaults.
 *
 * @author jast
 */
public class PlayerVaultListener implements Listener {

    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) {
            return;
        }

        // only interested in player vaults
        if (event.getType() != Type.PLAYER) {
            return;
        }

        SignChangeEvent cause = event.getCause();
        Component line2 = cause.line(2);

        if (line2 == null) {
            return;
        }

        String ownerName = PlainTextComponentSerializer.plainText().serialize(line2);

        Player player = cause.getPlayer();

        if (!CREATE_VAULT_PLAYER.isAllowed(player)) {
            player.sendMessage(LANG.vault_noVaultPerm);

            return;
        }

        AccountHolder owner;

        if (ownerName.length() > 0 && CREATE_VAULT_ADMIN.isAllowed(player)) {
            // attempting to create account for other player
            owner = Gringotts.getInstance().getAccountHolderFactory().get("player", ownerName);

            if (owner == null) {
                return;
            }
        } else {
            // regular vault creation for self
            owner = new PlayerAccountHolder(player);
        }

        event.setOwner(owner);
        event.setValid(true);
    }
}
