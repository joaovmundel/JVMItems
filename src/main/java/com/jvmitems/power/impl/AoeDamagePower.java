package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class AoeDamagePower extends Power {

    private double radius;
    private double damage;

    public AoeDamagePower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.AOE_DAMAGE, trigger, cooldownSeconds);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public void applyEffect(PowerContext context) {
        Location center = context.resolveLocation();
        for (Entity nearby : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(nearby instanceof LivingEntity) || nearby.equals(context.getUser())) {
                continue;
            }
            ((LivingEntity) nearby).damage(damage, context.getUser());
        }
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("radius", radius);
        section.set("damage", damage);
    }

    @Override
    public String describeExtra() {
        return "raio=" + radius + ", dano=" + damage;
    }

    public static AoeDamagePower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        AoeDamagePower power = new AoeDamagePower(trigger, cooldown);
        power.setRadius(section.getDouble("radius", 3.0));
        power.setDamage(section.getDouble("damage", 6.0));
        return power;
    }
}
