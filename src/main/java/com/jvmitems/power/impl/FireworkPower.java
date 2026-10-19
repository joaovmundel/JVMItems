package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.concurrent.ThreadLocalRandom;

public class FireworkPower extends Power {

    private int power = 1;

    public FireworkPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.FIREWORK, trigger, cooldownSeconds);
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public void applyEffect(PowerContext context) {
        Location location = context.getUser().getLocation();
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(org.bukkit.FireworkEffect.builder()
                .withColor(randomColor())
                .withFade(randomColor())
                .with(org.bukkit.FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .build());
        meta.setPower(power);
        firework.setFireworkMeta(meta);
    }

    private Color randomColor() {
        return Color.fromRGB(ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256));
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("power", power);
    }

    @Override
    public String describeExtra() {
        return "potencia=" + power;
    }

    public static FireworkPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        FireworkPower firework = new FireworkPower(trigger, cooldown);
        firework.setPower(section.getInt("power", 1));
        return firework;
    }
}
