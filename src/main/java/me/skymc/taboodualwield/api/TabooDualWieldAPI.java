package me.skymc.taboodualwield.api;

import me.skymc.taboodualwield.listener.PlayerListener;
import org.bukkit.entity.Player;

/**
 * @Author CziSKY
 * @Since 2021-03-05 15:11
 */
public class TabooDualWieldAPI {

    public static boolean isOffhandAttacking(Player player) {
        return PlayerListener.isOffhandAttacking(player);
    }

    public boolean isOffhandHasCooldown(Player player) {
        return PlayerListener.isOffhandHasCoolDown(player);
    }

    public double getPlayerOffhandCooldown(Player player) {

        if (!PlayerListener.getIsInCooldownList().contains(player.getUniqueId())) {
            return 0.0D;
        }

        return PlayerListener.getCooldownMap().get(player.getUniqueId());
    }

}
