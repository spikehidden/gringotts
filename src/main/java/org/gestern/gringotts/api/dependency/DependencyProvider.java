package org.gestern.gringotts.api.dependency;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * The interface Dependency provider.
 */
public interface DependencyProvider extends Iterable<Dependency> {
    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     *
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    static boolean packagesExists(String... packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Attempt to hook a plugin dependency.
     *
     * @param name       Name of the plugin.
     * @param classpath  classpath to check for
     * @param minVersion minimum version of the plugin. The plugin will still be hooked if this version is not
     *                   satisfied,
     *                   but a warning will be emitted.
     * @return the plugin object when hooked successfully, or null if not.
     */
    Plugin hookPlugin(String name, String classpath, String minVersion);

    /**
     * Register dependency boolean.
     *
     * @param dependency the dependency
     * @return the boolean
     */
    boolean registerDependency(Dependency dependency);

    /**
     * Gets dependency.
     *
     * @param id the name
     * @return the dependency
     */
    Optional<Dependency> getDependency(String id);

    boolean hasDependency(String id);

    default boolean isDependencyEnabled(String id) {
        return getDependency(id)
                .map(Dependency::isEnabled)
                .orElse(false);
    }

    /**
     * Gets dependencies.
     *
     * @return the dependencies
     */
    Collection<Dependency> getDependencies();

    /**
     * On load.
     */
    void onLoad();

    /**
     * On enable.
     */
    void onEnable();

    /**
     * On disable.
     */
    void onDisable();

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    @Override
    default Iterator<Dependency> iterator() {
        return getDependencies().iterator();
    }
}
