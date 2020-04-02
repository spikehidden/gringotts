package org.gestern.gringotts.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The type Search material.
 */
public class GetItemStackEvent extends Event implements Cancellable {
    /**
     * The constant handlers.
     */
    protected static final HandlerList handlers = new HandlerList();

    private final String material;
    private boolean canceled;
    private ItemStack stack;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     *
     * @param material the material
     */
    public GetItemStackEvent(String material) {
        this.material = material;
    }

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     *
     * @param isAsync  true indicates the event will fire asynchronously, false by default from default constructor
     * @param material the material
     */
    public GetItemStackEvent(boolean isAsync, String material) {
        super(isAsync);

        this.material = material;
    }

    /**
     * Gets handler list.
     *
     * @return the handler list
     */
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets material.
     *
     * @return the material
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Gets stack.
     *
     * @return the stack
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Sets stack.
     *
     * @param stack the stack
     */
    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return canceled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }
}
