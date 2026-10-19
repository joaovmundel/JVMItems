package com.jvmitems.power.impl;

import com.jvmitems.JVMItems;
import com.jvmitems.listeners.PlayerInteractListener;
import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LightningStrike;
import org.bukkit.metadata.FixedMetadataValue;

public class LightningPower extends Power {

    private boolean damaging = true;

    public LightningPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.LIGHTNING, trigger, cooldownSeconds);
    }

    public void setDamaging(boolean damaging) {
        this.damaging = damaging;
    }

    @Override
    public void applyEffect(PowerContext context) {
        Location location = context.resolveLocation();
        if (!damaging) {
            location.getWorld().strikeLightningEffect(location);
            return;
        }

        LightningStrike strike = location.getWorld().strikeLightning(location);

        JVMItems plugin = JVMItems.getInstance();
        if (strike != null && plugin != null) {
            strike.setMetadata(PlayerInteractListener.PROJECTILE_METADATA_KEY,
                    new FixedMetadataValue(plugin, context.getCustomItem().getId()));
        }
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("damaging", damaging);
    }

    @Override
    public String describeExtra() {
        return "causa-dano=" + damaging;
    }

    public static LightningPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        LightningPower power = new LightningPower(trigger, cooldown);
        power.setDamaging(section.getBoolean("damaging", true));
        return power;
    }
}
