package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class HealPower extends Power {

    private double amount;

    public HealPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.HEAL, trigger, cooldownSeconds);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public void applyEffect(PowerContext context) {
        Player user = context.getUser();
        user.setHealth(Math.min(user.getMaxHealth(), user.getHealth() + amount));
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("amount", amount);
    }

    @Override
    public String describeExtra() {
        return "cura=" + amount;
    }

    public static HealPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        HealPower power = new HealPower(trigger, cooldown);
        power.setAmount(section.getDouble("amount", 4.0));
        return power;
    }
}
