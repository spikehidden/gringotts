package org.gestern.gringotts.dependency.towny;

/**
 * Dummy implementation of towny handler, if the plugin cannot be loaded.
 *
 * @author jast
 */
class InvalidTownyHandler extends TownyHandler {
    /**
     * Return whether the plugin handled by this handler is enabled.
     *
     * @return whether the plugin handled by this handler is enabled.
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * Return whether the dependency is loaded into classpath.
     *
     * @return whether the dependency is loaded into classpath.
     */
    @Override
    public boolean isPresent() {
        return false;
    }
}