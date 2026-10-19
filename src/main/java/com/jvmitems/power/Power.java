package com.jvmitems.power;

import com.jvmitems.power.impl.AoeDamagePower;
import com.jvmitems.power.impl.CommandPower;
import com.jvmitems.power.impl.DamageMultiplierPower;
import com.jvmitems.power.impl.FireworkPower;
import com.jvmitems.power.impl.HealPower;
import com.jvmitems.power.impl.KnockbackPower;
import com.jvmitems.power.impl.LifestealPower;
import com.jvmitems.power.impl.LightningPower;
import com.jvmitems.power.impl.PotionEffectPower;
import com.jvmitems.power.impl.ProjectilePower;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Power {

    private final PowerType type;
    private Trigger trigger;
    private int cooldownSeconds;
    private final List<Condition> conditions = new ArrayList<>();

    protected Power(PowerType type, Trigger trigger, int cooldownSeconds) {
        this.type = type;
        this.trigger = trigger;
        this.cooldownSeconds = cooldownSeconds;
    }

    public PowerType getType() {
        return type;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public boolean checkConditions(PowerContext context) {
        for (Condition condition : conditions) {
            if (!condition.test(context)) {
                return false;
            }
        }
        return true;
    }

    public abstract void applyEffect(PowerContext context);

    protected abstract void saveExtra(ConfigurationSection section);

    public abstract String describeExtra();

    public final void save(ConfigurationSection section) {
        section.set("type", type.name());
        section.set("trigger", trigger.name());
        section.set("cooldown-seconds", cooldownSeconds);
        saveExtra(section);

        if (!conditions.isEmpty()) {
            List<Map<String, Object>> condMaps = new ArrayList<>();
            for (Condition condition : conditions) {
                YamlConfiguration temp = new YamlConfiguration();
                condition.save(temp);
                Map<String, Object> map = new LinkedHashMap<>();
                for (String key : temp.getKeys(false)) {
                    map.put(key, temp.get(key));
                }
                condMaps.add(map);
            }
            section.set("conditions", condMaps);
        }
    }

    @SuppressWarnings("unchecked")
    public static Power load(ConfigurationSection section) {
        PowerType type = PowerType.valueOf(section.getString("type").toUpperCase());
        Trigger trigger = Trigger.valueOf(section.getString("trigger").toUpperCase());
        int cooldown = section.getInt("cooldown-seconds", 0);

        Power power;
        switch (type) {
            case ARROW:
            case FIREBALL:
            case SNOWBALL:
                power = ProjectilePower.fromConfig(type, trigger, cooldown, section);
                break;
            case POTION:
                power = PotionEffectPower.fromConfig(trigger, cooldown, section);
                break;
            case LIFESTEAL:
                power = LifestealPower.fromConfig(trigger, cooldown, section);
                break;
            case DAMAGE_MULTIPLIER:
                power = DamageMultiplierPower.fromConfig(trigger, cooldown, section);
                break;
            case KNOCKBACK:
                power = KnockbackPower.fromConfig(trigger, cooldown, section);
                break;
            case COMMAND:
                power = CommandPower.fromConfig(trigger, cooldown, section);
                break;
            case LIGHTNING:
                power = LightningPower.fromConfig(trigger, cooldown, section);
                break;
            case HEAL:
                power = HealPower.fromConfig(trigger, cooldown, section);
                break;
            case FIREWORK:
                power = FireworkPower.fromConfig(trigger, cooldown, section);
                break;
            case AOE_DAMAGE:
                power = AoeDamagePower.fromConfig(trigger, cooldown, section);
                break;
            default:
                throw new IllegalArgumentException("Tipo de power desconhecido: " + type);
        }

        List<?> rawConditions = section.getList("conditions");
        if (rawConditions != null) {
            for (Object raw : rawConditions) {
                if (!(raw instanceof Map)) {
                    continue;
                }
                YamlConfiguration temp = new YamlConfiguration();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) raw).entrySet()) {
                    temp.set(String.valueOf(entry.getKey()), entry.getValue());
                }
                Condition condition = Condition.load(temp);
                if (condition != null) {
                    power.getConditions().add(condition);
                }
            }
        }

        return power;
    }

    public String describe() {
        String desc = type.name() + " @ " + trigger.name() + " (cooldown: " + cooldownSeconds + "s) - " + describeExtra();
        if (!conditions.isEmpty()) {
            List<String> condDescriptions = new ArrayList<>();
            for (Condition condition : conditions) {
                condDescriptions.add(condition.describe());
            }
            desc += " [condicoes: " + String.join(", ", condDescriptions) + "]";
        }
        return desc;
    }
}
