package me.skymc.taboodualwield.task;

import io.izzel.taboolib.module.locale.TLocale;
import me.skymc.taboodualwield.listener.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author CziSKY
 * @Since 2021-03-05 16:02
 */
public class CooldownTask extends BukkitRunnable {

    private double timer;
    private Player player;
    private boolean actionbarRender;

    public CooldownTask(Player player, Double cooldown, boolean actionbarRender) {
        this.timer = cooldown;
        this.player = player;
        this.actionbarRender = actionbarRender;
        PlayerListener.getCooldownMap().put(player.getUniqueId(), cooldown);
        PlayerListener.getIsInCooldownList().add(player.getUniqueId());
    }


    public void run() {
        if (this.timer < 0.2) {
            PlayerListener.getCooldownMap().remove(this.player.getUniqueId());
            PlayerListener.getIsInCooldownList().remove(this.player.getUniqueId());
            this.cancel();
        }

        this.timer -= 0.1;

        PlayerListener.getCooldownMap().put(this.player.getUniqueId(), Double.valueOf(String.format("%.2f", this.timer)));

        if (this.actionbarRender){
            TLocale.sendTo(this.player, "COOLDOWN-ACTION-BAR", Double.toString(PlayerListener.getCooldownMap().get(player.getUniqueId())));
        }
    }
}
