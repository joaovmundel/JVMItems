package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageMultiplierPower extends Power {

    private double multiplier;

    public DamageMultiplierPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.DAMAGE_MULTIPLIER, trigger, cooldownSeconds);
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void applyEffect(PowerContext context) {
        EntityDamageByEntityEvent event = context.getDamageEvent();
        if (event == null) {
            return;
        }
        event.setDamage(event.getDamage() * multiplier);
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("multiplier", multiplier);
    }

    @Override
    public String describeExtra() {
        return "multiplicador=x" + multiplier;
    }

    public static DamageMultiplierPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        DamageMultiplierPower power = new DamageMultiplierPower(trigger, cooldown);
        power.setMultiplier(section.getDouble("multiplier", 1.5));
        return power;
    }
}
