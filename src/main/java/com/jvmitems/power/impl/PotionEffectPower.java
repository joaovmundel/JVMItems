package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectPower extends Power {

    public enum Target {
        SELF,
        OPPONENT
    }

    private Target target;
    private PotionEffectType potionType;
    private int durationTicks;
    private int amplifier;

    public PotionEffectPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.POTION, trigger, cooldownSeconds);
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setPotionType(PotionEffectType potionType) {
        this.potionType = potionType;
    }

    public void setDurationTicks(int durationTicks) {
        this.durationTicks = durationTicks;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    @Override
    public void applyEffect(PowerContext context) {
        LivingEntity receiver = target == Target.OPPONENT ? context.getOpponent() : context.getUser();
        if (receiver == null) {
            return;
        }
        receiver.addPotionEffect(new PotionEffect(potionType, durationTicks, amplifier));
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("target", target.name());
        section.set("potion", potionType.getName());
        section.set("duration", durationTicks);
        section.set("amplifier", amplifier);
    }

    @Override
    public String describeExtra() {
        return "alvo=" + target + ", pocao=" + potionType.getName() + ", duracao=" + durationTicks + "t, amplificador=" + amplifier;
    }

    public static PotionEffectPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        PotionEffectPower power = new PotionEffectPower(trigger, cooldown);
        power.setTarget(Target.valueOf(section.getString("target", "OPPONENT").toUpperCase()));
        power.setPotionType(PotionEffectType.getByName(section.getString("potion", "SLOW")));
        power.setDurationTicks(section.getInt("duration", 100));
        power.setAmplifier(section.getInt("amplifier", 0));
        return power;
    }
}
