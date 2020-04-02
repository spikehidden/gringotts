package org.gestern.gringotts.material;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * The type Bukkit material provider.
 */
public class BukkitMaterialProvider implements MaterialProvider {
    /**
     * Match item stack optional.
     *
     * @param material the material
     * @param amount   the amount
     * @return the optional
     */
    @Override
    public Optional<ItemStack> matchItemStack(String material, int amount) {
        Material matchedMaterial = Material.matchMaterial(material);

        if (matchedMaterial == null) {
            return Optional.empty();
        }

        return Optional.of(new ItemStack(matchedMaterial, amount));
    }

    /**
     * Gets material name.
     *
     * @param material the material
     * @return the material name
     */
    @Override
    public Optional<String> getMaterialName(String material) {
        Material matchedMaterial = Material.matchMaterial(material);

        if (matchedMaterial == null) {
            return Optional.empty();
        }

        return Optional.of(matchedMaterial.toString());
    }
}
