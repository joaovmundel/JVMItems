package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class KnockbackPower extends Power {

    private double strength;

    public KnockbackPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.KNOCKBACK, trigger, cooldownSeconds);
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    @Override
    public void applyEffect(PowerContext context) {
        LivingEntity opponent = context.getOpponent();
        if (opponent == null) {
            return;
        }
        Vector push = opponent.getLocation().toVector().subtract(context.getUser().getLocation().toVector());
        push.setY(0);
        if (push.lengthSquared() < 0.0001) {
            push = context.getUser().getLocation().getDirection().setY(0);
        }
        if (push.lengthSquared() < 0.0001) {
            push = new Vector(1, 0, 0);
        }
        push.normalize().multiply(strength);
        push.setY(0.35);
        opponent.setVelocity(opponent.getVelocity().add(push));
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("strength", strength);
    }

    @Override
    public String describeExtra() {
        return "forca=" + strength;
    }

    public static KnockbackPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        KnockbackPower power = new KnockbackPower(trigger, cooldown);
        power.setStrength(section.getDouble("strength", 1.5));
        return power;
    }
}
