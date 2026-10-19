package com.jvmitems.power.impl;

import com.jvmitems.power.Power;
import com.jvmitems.power.PowerContext;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

public class CommandPower extends Power {

    private boolean console = true;
    private String command = "";

    public CommandPower(Trigger trigger, int cooldownSeconds) {
        super(PowerType.COMMAND, trigger, cooldownSeconds);
    }

    public void setConsole(boolean console) {
        this.console = console;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void applyEffect(PowerContext context) {
        String parsed = command.replace("%player%", context.getUser().getName());
        LivingEntity opponent = context.getOpponent();
        parsed = parsed.replace("%opponent%", opponent != null ? opponent.getName() : "");

        if (console) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        } else {
            Bukkit.dispatchCommand(context.getUser(), parsed);
        }
    }

    @Override
    protected void saveExtra(ConfigurationSection section) {
        section.set("console", console);
        section.set("command", command);
    }

    @Override
    public String describeExtra() {
        return "console=" + console + ", comando='" + command + "'";
    }

    public static CommandPower fromConfig(Trigger trigger, int cooldown, ConfigurationSection section) {
        CommandPower power = new CommandPower(trigger, cooldown);
        power.setConsole(section.getBoolean("console", true));
        power.setCommand(section.getString("command", ""));
        return power;
    }
}
