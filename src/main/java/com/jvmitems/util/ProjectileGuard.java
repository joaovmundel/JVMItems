package com.jvmitems.util;

import com.jvmitems.JVMItems;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;

import java.lang.reflect.Method;

public final class ProjectileGuard {

    private static Method pickupMethod;
    private static boolean pickupMethodResolved;

    private ProjectileGuard() {
    }

    public static void prepare(JVMItems plugin, Projectile projectile) {
        if (projectile instanceof Arrow && !plugin.allowProjectilePickup()) {
            disallowPickup(projectile);
        }
        if (plugin.antiGrief() && projectile instanceof Fireball) {
            ((Fireball) projectile).setIsIncendiary(false);
        }
        scheduleRemoval(plugin, projectile, plugin.projectileLifetimeSeconds() * 20L);
    }

    public static void removeSoon(JVMItems plugin, Entity entity) {
        scheduleRemoval(plugin, entity, 1L);
    }

    private static void scheduleRemoval(JVMItems plugin, final Entity entity, long delayTicks) {
        if (delayTicks <= 0) {
            return;
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (entity.isValid()) {
                    entity.remove();
                }
            }
        }, delayTicks);
    }

    private static void disallowPickup(Projectile projectile) {
        Method method = resolvePickupMethod(projectile);
        if (method == null) {
            return;
        }
        try {
            for (Object constant : method.getParameterTypes()[0].getEnumConstants()) {
                if (constant instanceof Enum && "DISALLOWED".equals(((Enum<?>) constant).name())) {
                    method.invoke(projectile, constant);
                    return;
                }
            }
        } catch (Exception ignored) {
            // versao sem a API de pickup; o projetil ainda e apagado pelo tempo de vida
        }
    }

    private static Method resolvePickupMethod(Projectile projectile) {
        if (!pickupMethodResolved) {
            pickupMethodResolved = true;
            for (Method candidate : projectile.getClass().getMethods()) {
                if (candidate.getName().equals("setPickupStatus")
                        && candidate.getParameterTypes().length == 1
                        && candidate.getParameterTypes()[0].isEnum()) {
                    pickupMethod = candidate;
                    break;
                }
            }
        }
        return pickupMethod;
    }
}
