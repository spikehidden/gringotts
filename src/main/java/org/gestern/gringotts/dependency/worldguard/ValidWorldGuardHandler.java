package org.gestern.gringotts.dependency.worldguard;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;

import java.util.UUID;

/**
 * The type Valid world guard handler.
 */
class ValidWorldGuardHandler extends WorldGuardHandler {
    private WorldGuardPlugin plugin;

    /**
     * Instantiates a new Valid world guard handler.
     *
     * @param plugin the plugin
     */
    public ValidWorldGuardHandler(WorldGuardPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new WorldGuardListener(this), Gringotts.getInstance());
        Gringotts.getInstance().registerAccountHolderProvider("worldguard", this);
    }

    /**
     * Enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isEnabled() {
        if (plugin != null) {
            return plugin.isEnabled();
        } else {
            return false;
        }
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
    public WorldGuardAccountHolder getAccountHolder(String id) {
        // FIXME use something more robust than - as world-id delimiter
        // try explicit world+id first
        String[] parts = id.split("-", 2);

        if (parts.length == 2) {
            WorldGuardAccountHolder wgah = getAccountHolder(parts[0], parts[1]);

            if (wgah != null) {
                return wgah;
            }
        }

        // try bare id in all worlds
        WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        for (World world : Bukkit.getWorlds()) {

            RegionManager worldManager = worldguardPlatform.getRegionContainer().get(new BukkitWorld(world));

            if (worldManager != null && worldManager.hasRegion(id)) {
                ProtectedRegion region = worldManager.getRegion(id);

                return new WorldGuardAccountHolder(world.getName(), region);
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

    /**
     * Get account holder for known world and region id.
     *
     * @param worldName name of world
     * @param id        worldguard region id
     * @return account holder for the region
     */
    public WorldGuardAccountHolder getAccountHolder(String worldName, String id) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        RegionManager worldManager = worldguardPlatform.getRegionContainer().get(new BukkitWorld(world));

        if (worldManager == null) {
            return null;
        }

        if (worldManager.hasRegion(id)) {
            ProtectedRegion region = worldManager.getRegion(id);

            return new WorldGuardAccountHolder(worldName, region);
        }

        return null;
    }
}
