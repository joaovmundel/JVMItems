package com.jvmitems.listeners;

import com.jvmitems.JVMItems;
import com.jvmitems.util.ProjectileGuard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.metadata.MetadataValue;

public class AntiGriefListener implements Listener {

    private final JVMItems plugin;

    public AntiGriefListener(JVMItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (ours(event.getEntity())) {
            event.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (ours(event.getEntity())) {
            event.setFire(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent event) {
        if (ours(event.getIgnitingEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChangeBlock(EntityChangeBlockEvent event) {
        if (ours(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        if (ours(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (ours(event.getRemover())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) && ours(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event instanceof Cancellable) || !ours(event.getEntity())) {
            return;
        }
        if (event.getHitBlock() != null && event.getHitEntity() == null) {
            ((Cancellable) event).setCancelled(true);
            ProjectileGuard.removeSoon(plugin, event.getEntity());
        }
    }

    private boolean ours(Entity entity) {
        if (entity == null || !plugin.antiGrief()) {
            return false;
        }
        if (!entity.hasMetadata(PlayerInteractListener.PROJECTILE_METADATA_KEY)) {
            return false;
        }
        for (MetadataValue value : entity.getMetadata(PlayerInteractListener.PROJECTILE_METADATA_KEY)) {
            if (value.getOwningPlugin() == plugin) {
                return true;
            }
        }
        return false;
    }
}
