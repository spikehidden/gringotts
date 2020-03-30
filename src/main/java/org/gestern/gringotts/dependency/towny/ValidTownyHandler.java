package org.gestern.gringotts.dependency.towny;

import com.palmergames.bukkit.towny.Towny;
import org.bukkit.Bukkit;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.dependency.towny.nation.NationHolderProvider;
import org.gestern.gringotts.dependency.towny.town.TownHolderProvider;
import org.gestern.gringotts.event.VaultCreationEvent;

/**
 * The type Valid towny handler.
 */
public class ValidTownyHandler extends TownyHandler {
    private final NationHolderProvider nationHolderProvider;
    private final TownHolderProvider townHolderProvider;
    private final Gringotts gringotts;
    private final Towny plugin;

    /**
     * Instantiates a new Valid towny handler.
     *
     * @param plugin the plugin
     */
    public ValidTownyHandler(Towny plugin) {
        this.plugin = plugin;
        this.gringotts = Gringotts.getInstance();

        this.nationHolderProvider = new NationHolderProvider(this, this.gringotts);
        this.townHolderProvider = new TownHolderProvider(this, this.gringotts);

        Bukkit.getPluginManager().registerEvents(new TownyListener(this), this.gringotts);

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

    public NationHolderProvider getNationHolderProvider() {
        return nationHolderProvider;
    }

    public TownHolderProvider getTownHolderProvider() {
        return townHolderProvider;
    }
}
