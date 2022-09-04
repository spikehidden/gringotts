package org.gestern.gringotts;

import org.bukkit.entity.Player;

/**
 * The enum Permissions.
 */
public enum Permissions {
    /**
     * Use vault inventory permissions.
     */
    USE_VAULT_INVENTORY("gringotts.usevault.inventory"),
    /**
     * Use vault enderchest permissions.
     */
    USE_VAULT_ENDERCHEST("gringotts.usevault.enderchest"),
    /**
     * Create vault admin permissions.
     */
    CREATE_VAULT_ADMIN("gringotts.createvault.admin"),
    /**
     * Create vault player permissions.
     */
    CREATE_VAULT_PLAYER("gringotts.createvault.player"),
    /**
     * Transfer permissions.
     */
    TRANSFER("gringotts.transfer"),
    /**
     * Command withdraw permissions.
     */
    COMMAND_WITHDRAW("gringotts.command.withdraw"),
    /**
     * Command deposit permissions.
     */
    COMMAND_DEPOSIT("gringotts.command.deposit");

    /**
     * The Node.
     */
    public final String node;

    Permissions(String node) {
        this.node = node;
    }

    /**
     * Check if a player has this permission.
     *
     * @param player player to check
     * @return whether given player has this permission
     */
    public boolean isAllowed(Player player) {
        return player.hasPermission(this.node);
    }
}
