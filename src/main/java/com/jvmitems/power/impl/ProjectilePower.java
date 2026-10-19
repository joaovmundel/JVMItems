package com.jvmitems.power.impl;

import com.jvmitems.JVMItems;
import com.jvmitems.listeners.PlayerInteractListener;
import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import com.jvmitems.util.ProjectileGuard;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ProjectilePower extends Power {

    private final JVMItems plugin;
    private int amount = 1;
    private double speed = 1.5;
    private double spread = 0.0;
    private boolean incendiary = false;
    private boolean explosive = false;

    public ProjectilePower(JVMItems plugin, PowerType type, Trigger trigger, int cooldownSeconds) {
        super(type, trigger, cooldownSeconds);
        this.plugin = plugin;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(1, amount);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }

    public void setIncendiary(boolean incendiary) {
        this.incendiary = incendiary;
    }

    public void setExplosive(boolean explosive) {
        this.explosive = explosive;
    }

    @Override
    public void applyEffect(PowerContext context) {
        Player player = context.getUser();
        double baseYaw = Math.toRadians(player.getLocation().getYaw());

        for (int i = 0; i < amount; i++) {
            double spreadOffset = 0;
            if (amount > 1 && spread > 0) {
                spreadOffset = Math.toRadians(-spread / 2 + (spread * i / (amount - 1)));
            }

            Vector direction = player.getLocation().getDirection().clone();
            double angle = baseYaw + spreadOffset;
            double length = Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ());
            direction.setX(-Math.sin(angle) * length);
            direction.setZ(Math.cos(angle) * length);

            Projectile projectile = player.launchProjectile(projectileClass(), direction.multiply(speed));
            projectile.setMetadata(PlayerInteractListener.PROJECTILE_METADATA_KEY,
                    new FixedMetadataValue(plugin, context.getCustomItem().getId()));

            if (projectile instanceof Fireball) {
                Fireball fireball = (Fireball) projectile;
                fireball.setIsIncendiary(incendiary);
                fireball.setYield(explosive ? fireball.getYield() : 0f);
            }

            ProjectileGuard.prepare(plugin, projectile);
        }
    }

    private Class<? extends Projectile> projectileClass() {
        switch (getType()) {
            case ARROW:
                return Arrow.class;
            case FIREBALL:
                return Fireball.class;
            case SNOWBALL:
            default:
                return Snowball.class;
        }
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("amount", amount);
        section.set("speed", speed);
        section.set("spread", spread);
        section.set("incendiary", incendiary);
        section.set("explosive", explosive);
    }

    @Override
    public String describeExtra() {
        return "quantidade=" + amount + ", velocidade=" + speed + ", espalhamento=" + spread
                + (getType() == PowerType.FIREBALL ? ", incendiary=" + incendiary + ", explosive=" + explosive : "");
    }

    public static ProjectilePower fromConfig(PowerType type, Trigger trigger, int cooldown, ConfigurationSection section) {
        ProjectilePower power = new ProjectilePower(JVMItems.getInstance(), type, trigger, cooldown);
        power.setAmount(section.getInt("amount", 1));
        power.setSpeed(section.getDouble("speed", 1.5));
        power.setSpread(section.getDouble("spread", 0.0));
        power.setIncendiary(section.getBoolean("incendiary", false));
        power.setExplosive(section.getBoolean("explosive", false));
        return power;
    }
}
