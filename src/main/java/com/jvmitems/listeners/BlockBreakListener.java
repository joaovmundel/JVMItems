package com.jvmitems.listeners;

import com.jvmitems.JVMItems;
import com.jvmitems.item.CustomItem;
import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.Trigger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    private final JVMItems plugin;

    public BlockBreakListener(JVMItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        String itemId = plugin.getItemManager().identify(item);
        if (itemId == null) {
            return;
        }
        CustomItem customItem = plugin.getItemManager().get(itemId);
        if (customItem == null || customItem.getPowers().isEmpty()) {
            return;
        }

        for (int i = 0; i < customItem.getPowers().size(); i++) {
            Power power = customItem.getPowers().get(i);
            if (power.getTrigger() != Trigger.BLOCK_BREAK) {
                continue;
            }

            String cooldownKey = itemId + ":" + i;
            if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), cooldownKey, power.getCooldownSeconds())) {
                continue;
            }

            PowerContext context = new PowerContext(player, item, customItem, Trigger.BLOCK_BREAK).block(event.getBlock());
            if (!power.checkConditions(context)) {
                continue;
            }

            power.applyEffect(context);
            plugin.getCooldownManager().setUsed(player.getUniqueId(), cooldownKey);
        }
    }
}
