package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LifestealPower extends Power {

    private double percent;

    public LifestealPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.LIFESTEAL, trigger, cooldownSeconds);
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public void applyEffect(PowerContext context) {
        Player user = context.getUser();
        double healAmount = context.getDamageAmount() * percent;
        if (healAmount <= 0) {
            return;
        }
        user.setHealth(Math.min(user.getMaxHealth(), user.getHealth() + healAmount));
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("percent", percent);
    }

    @Override
    public String describeExtra() {
        return "cura=" + (percent * 100) + "% do dano causado";
    }

    public static LifestealPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        LifestealPower power = new LifestealPower(trigger, cooldown);
        power.setPercent(section.getDouble("percent", 0.2));
        return power;
    }
}
