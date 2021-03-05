package me.skymc.taboodualwield;

import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.logger.TLogger;
import io.izzel.taboolib.module.packet.TPacketHandler;
import io.izzel.taboolib.module.packet.TPacketListener;
import me.skymc.taboodualwield.listener.PlayerListener;
import org.bukkit.entity.Player;

/**
 * @Author CziSKY
 * @Since 2021-03-05 14:05
 */
public class TabooDualWield extends Plugin {

    @TInject("config.yml")
    private static TConfig conf;

    @Override
    public void onEnable() {

        conf.listener(() -> TLogger.getGlobalLogger().fine("configuration loaded.")).runListener();

        TPacketHandler.addListener(this.getPlugin(), new TPacketListener() {
            @Override
            public boolean onSend(Player player, Object packet) {
                String name = packet.getClass().getSimpleName();

                if (PlayerListener.isOffhandAttacking(player)) {
                    return !name.equals("PacketPlayOutSetSlot") && !name.equals("PacketPlayOutWindowItems");
                }

                return true;
            }
        });
    }

    public static TConfig getConf() {
        return conf;
    }
}
