package org.gestern.gringotts.material;

import com.jojodmo.customitems.api.CustomItemsAPI;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * The type Custom items material provider.
 */
public class CustomItemsMaterialProvider implements MaterialProvider {
    /**
     * Match item stack optional.
     *
     * @param material the material
     * @param amount   the amount
     * @return the optional
     */
    @Override
    public Optional<ItemStack> matchItemStack(String material, int amount) {
        //noinspection ConstantConditions
        return Optional.ofNullable(CustomItemsAPI.getCustomItem(material, amount));
    }

    /**
     * Gets material name.
     *
     * @param material the material
     * @return the material name
     */
    @Override
    public Optional<String> getMaterialName(String material) {
        //noinspection ConstantConditions
        return Optional.ofNullable(CustomItemsAPI.getCustomItemFriendlyName(material));
    }
}
