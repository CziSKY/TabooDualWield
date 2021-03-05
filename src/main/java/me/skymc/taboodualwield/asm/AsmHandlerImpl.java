package me.skymc.taboodualwield.asm;

import io.izzel.taboolib.module.compat.PermissionHook;
import io.izzel.taboolib.module.packet.TPacketHandler;
import me.skymc.taboodualwield.TabooDualWield;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.ItemStack;
import net.minecraft.server.v1_16_R1.PacketPlayOutAnimation;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author CziSKY
 * @Since 2021-03-05 14:17
 */
public class AsmHandlerImpl extends AsmHandler {

    public void attack(Player player, Entity entity){
        AtomicBoolean isBypassedCheck = new AtomicBoolean(false);

        TabooDualWield
                .getConf()
                .getStringList("BYPASS-ANTICHEAT-PERMISSIONS")
                .stream()
                .peek(s -> {
                    if (! s.equalsIgnoreCase("NONE")){
                        isBypassedCheck.set(true);
                        return;
                    }
                    PermissionHook.add(player, s);
                });
        ((CraftPlayer) player).getHandle().attack(((CraftLivingEntity) entity).getHandle());

        if (isBypassedCheck.get()){
            return;
        }

        TabooDualWield
                .getConf()
                .getStringList("BYPASS-ANTICHEAT-PERMISSIONS")
                .stream()
                .peek(s -> PermissionHook.remove(player, s));
    }

    public void offhandDisplay(Player player){
        TPacketHandler.sendPacket(player, new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 3));
    }

    public void toggleHand(Player player){
        try {
            Object entityPlayer = ((CraftPlayer) player).getHandle();
            Object itemInMainHand = org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
            Object itemInOffHand = org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand());

            ((EntityPlayer) entityPlayer).inventory.items.set(((EntityPlayer) entityPlayer).inventory.itemInHandIndex, (ItemStack) itemInOffHand);
            ((EntityPlayer) entityPlayer).inventory.extraSlots.set(0, (ItemStack) itemInMainHand);

        } catch (Throwable t){
            t.printStackTrace();
        }
    }

}
