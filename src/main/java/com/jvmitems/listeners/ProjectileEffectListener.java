package com.jvmitems.listeners;

import com.jvmitems.JVMItems;
import com.jvmitems.item.CustomItem;
import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.Trigger;
import com.jvmitems.util.ProjectileGuard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileEffectListener implements Listener {

    private final JVMItems plugin;

    public ProjectileEffectListener(JVMItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!projectile.hasMetadata(PlayerInteractListener.PROJECTILE_METADATA_KEY)) {
            return;
        }

        String itemId = null;
        for (MetadataValue value : projectile.getMetadata(PlayerInteractListener.PROJECTILE_METADATA_KEY)) {
            if (value.getOwningPlugin() == plugin) {
                itemId = value.asString();
                break;
            }
        }
        if (itemId == null) {
            return;
        }

        if (plugin.removeProjectileOnHit()) {
            ProjectileGuard.removeSoon(plugin, projectile);
        }

        CustomItem customItem = plugin.getItemManager().get(itemId);
        if (customItem == null || customItem.getPowers().isEmpty()) {
            return;
        }

        ProjectileSource source = projectile.getShooter();
        if (!(source instanceof Player)) {
            return;
        }
        Player shooter = (Player) source;

        Entity hit = event.getHitEntity();
        LivingEntity opponent = hit instanceof LivingEntity ? (LivingEntity) hit : null;

        ItemStack item = shooter.getInventory().getItemInMainHand();

        for (int i = 0; i < customItem.getPowers().size(); i++) {
            Power power = customItem.getPowers().get(i);
            if (power.getTrigger() != Trigger.PROJECTILE_HIT) {
                continue;
            }

            String cooldownKey = itemId + ":" + i;
            if (plugin.getCooldownManager().isOnCooldown(shooter.getUniqueId(), cooldownKey, power.getCooldownSeconds())) {
                continue;
            }

            PowerContext context = new PowerContext(shooter, item, customItem, Trigger.PROJECTILE_HIT).opponent(opponent);
            if (!power.checkConditions(context)) {
                continue;
            }

            power.applyEffect(context);
            plugin.getCooldownManager().setUsed(shooter.getUniqueId(), cooldownKey);
        }
    }
}
