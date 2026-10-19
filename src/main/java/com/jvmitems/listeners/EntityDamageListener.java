package com.jvmitems.listeners;

import com.jvmitems.JVMItems;
import com.jvmitems.item.CustomItem;
import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EntityDamageListener implements Listener {

    private final JVMItems plugin;

    public EntityDamageListener(JVMItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamagePre(EntityDamageByEntityEvent event) {
        process(event, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageResolved(EntityDamageByEntityEvent event) {
        process(event, false);
    }

    private void process(EntityDamageByEntityEvent event, boolean onlyDamageMultiplier) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity victim = (LivingEntity) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack hand = attacker.getInventory().getItemInMainHand();
            run(hand, Trigger.HIT, attacker, victim, event, onlyDamageMultiplier);
        }

        if (victim instanceof Player) {
            Player defender = (Player) victim;
            LivingEntity opponent = event.getDamager() instanceof LivingEntity ? (LivingEntity) event.getDamager() : null;
            PlayerInventory inventory = defender.getInventory();
            run(inventory.getItemInMainHand(), Trigger.TAKE_DAMAGE, defender, opponent, event, onlyDamageMultiplier);
            run(inventory.getItemInOffHand(), Trigger.TAKE_DAMAGE, defender, opponent, event, onlyDamageMultiplier);
            run(inventory.getHelmet(), Trigger.TAKE_DAMAGE, defender, opponent, event, onlyDamageMultiplier);
            run(inventory.getChestplate(), Trigger.TAKE_DAMAGE, defender, opponent, event, onlyDamageMultiplier);
            run(inventory.getLeggings(), Trigger.TAKE_DAMAGE, defender, opponent, event, onlyDamageMultiplier);
            run(inventory.getBoots(), Trigger.TAKE_DAMAGE, defender, opponent, event, onlyDamageMultiplier);
        }
    }

    private void run(ItemStack item, Trigger trigger, Player user, LivingEntity opponent,
                      EntityDamageByEntityEvent event, boolean onlyDamageMultiplier) {
        if (item == null) {
            return;
        }
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
            if (power.getTrigger() != trigger) {
                continue;
            }
            boolean isDamageMultiplier = power.getType() == PowerType.DAMAGE_MULTIPLIER;
            if (onlyDamageMultiplier != isDamageMultiplier) {
                continue;
            }

            String cooldownKey = itemId + ":" + i;
            if (plugin.getCooldownManager().isOnCooldown(user.getUniqueId(), cooldownKey, power.getCooldownSeconds())) {
                continue;
            }

            PowerContext context = new PowerContext(user, item, customItem, trigger)
                    .opponent(opponent)
                    .damageEvent(event)
                    .damageAmount(event.getFinalDamage());
            if (!power.checkConditions(context)) {
                continue;
            }

            power.applyEffect(context);
            plugin.getCooldownManager().setUsed(user.getUniqueId(), cooldownKey);
        }
    }
}
