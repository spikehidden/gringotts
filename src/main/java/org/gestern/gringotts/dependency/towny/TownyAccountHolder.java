package org.gestern.gringotts.dependency.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.*;
import org.gestern.gringotts.accountholder.AccountHolder;

/**
 * The type Towny account holder.
 */
class TownyAccountHolder implements AccountHolder {
    /**
     * The Owner.
     */
    public final EconomyHandler owner;
    /**
     * The Type.
     */
    public final String type;

    /**
     * Instantiates a new Towny account holder.
     *
     * @param owner the owner
     * @param type  the type
     */
    public TownyAccountHolder(EconomyHandler owner, String type) {
        this.owner = owner;
        this.type = type;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return owner instanceof Nameable ? ((Nameable) owner).getName() : "Unable to find a name";
    }

    /**
     * Send a message to all online players of the specified {@link Town} or {@link Nation}
     *
     * @param message to send
     */
    @Override
    public void sendMessage(String message) {
        if (owner instanceof ResidentList) {
            TownyAPI.getInstance()
                    .getOnlinePlayers((ResidentList) owner)
                    .forEach(player -> player.sendMessage(message));
        }
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public String getId() {
        return owner.getAccount().getName();
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return String.format(
                "TownyAccountHolder(%1$s)",
                ((Nameable) owner).getName()
        );
    }
}
