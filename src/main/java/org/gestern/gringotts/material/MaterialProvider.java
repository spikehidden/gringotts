package org.gestern.gringotts.material;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * The interface Material provider.
 */
public interface MaterialProvider {
    /**
     * Match item stack optional.
     *
     * @param material the material
     * @return the optional
     */
    default Optional<ItemStack> matchItemStack(String material) {
        return matchItemStack(material, 0);
    }

    /**
     * Match item stack optional.
     *
     * @param material the material
     * @param amount   the amount
     * @return the optional
     */
    Optional<ItemStack> matchItemStack(String material, int amount);

    /**
     * Gets material name.
     *
     * @param material the material
     * @return the material name
     */
    Optional<String> getMaterialName(String material);
}
