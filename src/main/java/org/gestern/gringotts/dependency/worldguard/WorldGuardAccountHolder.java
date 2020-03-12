package org.gestern.gringotts.dependency.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gestern.gringotts.accountholder.AccountHolder;

import java.util.UUID;

/**
 * The type World guard account holder.
 */
class WorldGuardAccountHolder implements AccountHolder {
    /**
     * The World.
     */
    final String world;
    /**
     * The Region.
     */
    final ProtectedRegion region;

    /**
     * Instantiates a new World guard account holder.
     *
     * @param world  the world
     * @param region the region
     */
    public WorldGuardAccountHolder(String world, ProtectedRegion region) {
        this.world = world;
        this.region = region;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return region.getId();
    }

    /**
     * Send message.
     *
     * @param message the message
     */
    @Override
    public void sendMessage(String message) {
        //Send the message to owners.
        for (UUID uuid : region.getOwners().getUniqueIds()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.sendMessage(message);
            }
        }

        //Send the message to members.
        for (UUID uuid : region.getMembers().getUniqueIds()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return "worldguard";
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public String getId() {
        return world + "-" + region.getId();
    }
}
