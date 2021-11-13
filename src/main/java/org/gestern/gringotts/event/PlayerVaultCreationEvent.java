package org.gestern.gringotts.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Vault creation event triggered by a player.
 *
 * @author jast
 */
public class PlayerVaultCreationEvent extends VaultCreationEvent {

    private final SignChangeEvent cause;

    public PlayerVaultCreationEvent(Type type, SignChangeEvent cause) {
        super(type);

        this.cause = cause;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return VaultCreationEvent.handlers;
    }

    /**
     * Get the player involved in creating the vault.
     *
     * @return the player involved in creating the vault
     */
    public SignChangeEvent getCause() {
        return cause;
    }

}
