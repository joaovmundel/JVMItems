package com.jvmitems.power;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class Condition {

    public enum Type {
        PERMISSION,
        SNEAKING,
        NOT_SNEAKING,
        HEALTH_BELOW,
        HEALTH_ABOVE,
        CHANCE
    }

    private final Type type;
    private String permission;
    private double value;

    private Condition(Type type) {
        this.type = type;
    }

    public static Condition permission(String node) {
        Condition condition = new Condition(Type.PERMISSION);
        condition.permission = node;
        return condition;
    }

    public static Condition sneaking(boolean required) {
        Condition condition = new Condition(required ? Type.SNEAKING : Type.NOT_SNEAKING);
        return condition;
    }

    public static Condition healthBelow(double percent) {
        Condition condition = new Condition(Type.HEALTH_BELOW);
        condition.value = percent;
        return condition;
    }

    public static Condition healthAbove(double percent) {
        Condition condition = new Condition(Type.HEALTH_ABOVE);
        condition.value = percent;
        return condition;
    }

    public static Condition chance(double probability) {
        Condition condition = new Condition(Type.CHANCE);
        condition.value = probability;
        return condition;
    }

    public Type getType() {
        return type;
    }

    public boolean test(PowerContext context) {
        Player user = context.getUser();
        switch (type) {
            case PERMISSION:
                return user.hasPermission(permission);
            case SNEAKING:
                return user.isSneaking();
            case NOT_SNEAKING:
                return !user.isSneaking();
            case HEALTH_BELOW:
                return healthRatio(user) < value;
            case HEALTH_ABOVE:
                return healthRatio(user) > value;
            case CHANCE:
                return ThreadLocalRandom.current().nextDouble() <= value;
            default:
                return true;
        }
    }

    private double healthRatio(Player user) {
        double max = user.getMaxHealth();
        return max <= 0 ? 0 : user.getHealth() / max;
    }

    public void save(ConfigurationSection section) {
        section.set("type", type.name());
        if (type == Type.PERMISSION) {
            section.set("permission", permission);
        } else if (type == Type.HEALTH_BELOW || type == Type.HEALTH_ABOVE || type == Type.CHANCE) {
            section.set("value", value);
        }
    }

    public static Condition load(ConfigurationSection section) {
        Type type = Type.valueOf(section.getString("type").toUpperCase());
        switch (type) {
            case PERMISSION:
                return permission(section.getString("permission", ""));
            case SNEAKING:
                return sneaking(true);
            case NOT_SNEAKING:
                return sneaking(false);
            case HEALTH_BELOW:
                return healthBelow(section.getDouble("value", 0.5));
            case HEALTH_ABOVE:
                return healthAbove(section.getDouble("value", 0.5));
            case CHANCE:
                return chance(section.getDouble("value", 1.0));
            default:
                return null;
        }
    }

    public String describe() {
        switch (type) {
            case PERMISSION:
                return "Permissao: " + permission;
            case SNEAKING:
                return "Precisa estar agachado";
            case NOT_SNEAKING:
                return "Precisa NAO estar agachado";
            case HEALTH_BELOW:
                return "Vida abaixo de " + (value * 100) + "%";
            case HEALTH_ABOVE:
                return "Vida acima de " + (value * 100) + "%";
            case CHANCE:
                return "Chance de " + (value * 100) + "%";
            default:
                return type.name();
        }
    }
}
