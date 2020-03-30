package org.gestern.gringotts.dependency.worldguard;

import com.sk89q.worldguard.domains.DefaultDomain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;
import org.gestern.gringotts.event.VaultCreationEvent;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.CREATE_VAULT_ADMIN;
import static org.gestern.gringotts.Permissions.CREATE_VAULT_WORLDGUARD;

/**
 * The type World guard listener.
 */
public class WorldGuardListener implements Listener {
    private final ValidWorldGuardHandler handler;

    /**
     * Instantiates a new World guard listener.
     *
     * @param handler the handler
     */
    protected WorldGuardListener(ValidWorldGuardHandler handler) {
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
        if (event.isValid()) {
            return;
        }

        if (event.getType() == VaultCreationEvent.Type.REGION) {
            Player player = event.getCause().getPlayer();

            if (!CREATE_VAULT_WORLDGUARD.isAllowed(player)) {
                player.sendMessage(LANG.plugin_worldguard_noVaultPerm);

                return;
            }

            String regionId = event.getCause().getLine(2);
            String[] regionComponents = regionId.split("-", 1);

            WorldGuardAccountHolder owner;
            if (regionComponents.length == 1) {
                // try to guess the world
                owner = this.handler.getAccountHolder(regionComponents[0]);
            } else {
                String world = regionComponents[0];
                String id = regionComponents[1];
                owner = this.handler.getAccountHolder(world, id);
            }

            if (owner != null && (owner.region.hasMembersOrOwners() || CREATE_VAULT_ADMIN.isAllowed(player))) {
                DefaultDomain regionOwners = owner.region.getOwners();

                if (regionOwners.contains(player.getName())) {
                    event.setOwner(owner);
                    event.setValid(true);
                }
            }
        }
    }
}
