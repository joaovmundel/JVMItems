package com.jvmitems.listeners;

import com.jvmitems.JVMItems;
import com.jvmitems.item.CustomItem;
import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.Trigger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    public static final String PROJECTILE_METADATA_KEY = "jvmitems_item_id";

    private final JVMItems plugin;

    public PlayerInteractListener(JVMItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != null && event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Trigger trigger;
        boolean sneaking = event.getPlayer().isSneaking();
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                trigger = sneaking ? Trigger.SNEAK_RIGHT_CLICK : Trigger.RIGHT_CLICK;
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                trigger = sneaking ? Trigger.SNEAK_LEFT_CLICK : Trigger.LEFT_CLICK;
                break;
            default:
                return;
        }

        ItemStack item = event.getItem();
        String itemId = plugin.getItemManager().identify(item);
        if (itemId == null) {
            return;
        }
        CustomItem customItem = plugin.getItemManager().get(itemId);
        if (customItem == null || customItem.getPowers().isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        boolean triggered = false;
        long bestRemaining = -1;

        for (int i = 0; i < customItem.getPowers().size(); i++) {
            Power power = customItem.getPowers().get(i);
            if (power.getTrigger() != trigger) {
                continue;
            }

            String cooldownKey = itemId + ":" + i;
            if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), cooldownKey, power.getCooldownSeconds())) {
                long remaining = plugin.getCooldownManager().remainingSeconds(player.getUniqueId(), cooldownKey, power.getCooldownSeconds());
                if (bestRemaining == -1 || remaining < bestRemaining) {
                    bestRemaining = remaining;
                }
                continue;
            }

            PowerContext context = new PowerContext(player, item, customItem, trigger);
            if (!power.checkConditions(context)) {
                continue;
            }

            power.applyEffect(context);
            plugin.getCooldownManager().setUsed(player.getUniqueId(), cooldownKey);
            triggered = true;
        }

        if (triggered) {
            event.setCancelled(true);
        } else if (bestRemaining > 0) {
            player.sendMessage(plugin.message("cooldown", "segundos", String.valueOf(bestRemaining)));
            event.setCancelled(true);
        }
    }
}
