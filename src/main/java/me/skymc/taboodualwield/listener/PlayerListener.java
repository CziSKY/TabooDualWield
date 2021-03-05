package me.skymc.taboodualwield.listener;

import io.izzel.taboolib.module.compat.PermissionHook;
import io.izzel.taboolib.module.inject.TListener;
import io.izzel.taboolib.module.locale.TLocale;
import me.skymc.taboodualwield.TabooDualWield;
import me.skymc.taboodualwield.api.event.OffhandAnimatonEvent;
import me.skymc.taboodualwield.api.event.OffhandAttackEvent;
import me.skymc.taboodualwield.task.CooldownTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.skymc.taboodualwield.asm.AsmHandler.getImpl;

/**
 * @Author CziSKY
 * @Since 2021-03-05 14:33
 */
@TListener
public class PlayerListener implements Listener {

    private static final ArrayList<UUID> IS_IN_COOLDOWN_LIST = new ArrayList<>();
    private static final HashMap<UUID, Double> COOLDOWN_MAP = new HashMap<>();

    @EventHandler
    public void onClickOnAir(PlayerInteractEvent e) {

        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
            return;
        }

        Player player = e.getPlayer();

        OffhandAnimatonEvent event = new OffhandAnimatonEvent(player, player.getInventory().getItemInOffHand());

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (!attackRequireCheck(player) || isOffhandHasCoolDown(player)) {
            return;
        }

        getImpl().offhandDisplay(player);

        double cooldownTicks = getItemCooldownRequireIfExist(player);

        if (cooldownTicks > 0.0D) {
            makeOffhandCooldown(player, cooldownTicks);
        }

    }

    @EventHandler
    public void onClickOnEntity(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof LivingEntity)) {
            return;
        }
        Player player = e.getPlayer();

        OffhandAttackEvent event = new OffhandAttackEvent(player, player.getInventory().getItemInOffHand());

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (!attackRequireCheck(player) || isOffhandHasCoolDown(player)) {
            return;
        }

        player.setMetadata("OFFHAND-ATTACKING", new FixedMetadataValue(TabooDualWield.getInstance().getPlugin(), "0"));

        getImpl().toggleHand(player);
        getImpl().attack(player, e.getRightClicked());
        getImpl().toggleHand(player);

        player.updateInventory();

        player.removeMetadata("OFFHAND-ATTACKING", TabooDualWield.getInstance().getPlugin());
    }

    public boolean attackRequireCheck(Player player) {
        ItemStack itemStackInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemStackInOffHand = player.getInventory().getItemInOffHand();

        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
            return false;
        }

        if (!Objects.requireNonNull(TabooDualWield.getConf().getString("ATTACK-REQUIRE.PERMISSION")).equalsIgnoreCase("NONE") && !PermissionHook.has(player, TabooDualWield.getConf().getString("ATTACK-REQUIRE.PERMISSION"))) {
            return false;
        }

        if (TabooDualWield.getConf().getBoolean("ATTACK-REQUIRE.FOOD-LEVEL") && itemStackInMainHand.getType().isEdible() && !(player.getFoodLevel() == 20)) {
            return false;
        }

        if (!Objects.requireNonNull(TabooDualWield.getConf().getString("ATTACK-REQUIRE.LORE-KEY")).equalsIgnoreCase("NONE") && !itemStackInOffHand.hasItemMeta() && !Objects.requireNonNull(itemStackInOffHand.getItemMeta()).hasLore()) {
            return false;
        }

        if (!Objects.requireNonNull(TabooDualWield.getConf().getString("ATTACK-REQUIRE.LORE-KEY")).equalsIgnoreCase("NONE") && !itemStackInOffHand.hasItemMeta() && !Objects.requireNonNull(itemStackInOffHand.getItemMeta()).hasLore()) {
            return Objects.requireNonNull(Objects.requireNonNull(player.getInventory().getItemInOffHand().getItemMeta()).getLore()).toString().contains(Objects.requireNonNull(TabooDualWield.getConf().getString("ATTACK-REQUIRE.LORE-KEY")));
        }

        return true;
    }

    public static boolean isOffhandHasCoolDown(Player player) {
        return IS_IN_COOLDOWN_LIST.contains(player.getUniqueId()) && COOLDOWN_MAP.containsKey(player.getUniqueId());
    }

    public static boolean isOffhandAttacking(Player player) {
        return player.hasMetadata("OFFHAND-ATTACKING");
    }

    public void makeOffhandCooldown(Player player, Double tick) {
        boolean actionbarRender = TabooDualWield.getConf().getBoolean("ATTACK-COOLDOWN.ACTIONBAR-DISPLAY");

        new CooldownTask(player, tick, actionbarRender).runTaskTimerAsynchronously(TabooDualWield.getInstance().getPlugin(), 0L, 2L);

        if (TabooDualWield.getConf().getBoolean("ATTACK-COOLDOWN.OFFHAND-BAR-DISPLAY")) {
            player.setCooldown(player.getInventory().getItemInOffHand().getType(), Integer.parseInt(new DecimalFormat("0").format(tick * 20.0)));
        }
    }

    public double getItemCooldownRequireIfExist(Player player) {
        if (!player.getInventory().getItemInOffHand().hasItemMeta() && player.getInventory().getItemInOffHand().getItemMeta().hasLore()) {
            return 0.0;
        }

        List<String> itemLores = player.getInventory().getItemInOffHand().getItemMeta().getLore();
        Pattern triggerPattern = Pattern.compile(TabooDualWield.getConf().getString("ATTACK-COOLDOWN.LORE-KEY").replace("{0}", "(?<num>\\S+)"));

        if (itemLores == null) {
            return 0.0;
        }

        for (String itemLore : itemLores) {
            Matcher lorePattern = triggerPattern.matcher(itemLore);

            if (lorePattern.find()) {
                String eventNumber = lorePattern.group("num");

                if (eventNumber == null) {
                    return 0.0;
                }

                return Double.parseDouble(eventNumber);
            }
        }
        return 0.0;
    }

    public static ArrayList<UUID> getIsInCooldownList() {
        return IS_IN_COOLDOWN_LIST;
    }

    public static HashMap<UUID, Double> getCooldownMap() {
        return COOLDOWN_MAP;
    }

}
