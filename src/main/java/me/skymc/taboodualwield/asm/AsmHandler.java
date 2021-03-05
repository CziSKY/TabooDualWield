package me.skymc.taboodualwield.asm;

import io.izzel.taboolib.module.inject.TFunction;
import io.izzel.taboolib.module.lite.SimpleVersionControl;
import me.skymc.taboodualwield.TabooDualWield;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @Author CziSKY
 * @Since 2021-03-05 14:15
 */
@TFunction(enable = "init")
public abstract class AsmHandler {

    private static AsmHandler impl;

    public static AsmHandler getImpl() {
        return AsmHandler.impl;
    }

    static void init() {
        try {
            AsmHandler.impl = (AsmHandler) SimpleVersionControl.createNMS("me.skymc.taboodualwield.asm.AsmHandlerImpl")
                    .useCache()
                    .translate(TabooDualWield.getInstance().getPlugin())
                    .newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public abstract void attack(Player player, Entity entity);
    public abstract void offhandDisplay(Player player);
    public abstract void toggleHand(Player player);


}
