package me.skymc.taboodualwield.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @Author CziSKY
 * @Since 2021-03-05 15:16
 */
public class OffhandAttackEvent extends Event implements Cancellable {
    private final Player player;
    private final ItemStack offhandItem;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public OffhandAttackEvent(Player player, ItemStack offhandItem) {
        this.player = player;
        this.offhandItem = offhandItem;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getOffhandItem() {
        return this.offhandItem;
    }

}
