package com.jvmitems;

import com.jvmitems.commands.JVMItemsCommand;
import com.jvmitems.commands.JVMItemsTabCompleter;
import com.jvmitems.item.ItemManager;
import com.jvmitems.listeners.AntiGriefListener;
import com.jvmitems.listeners.BlockBreakListener;
import com.jvmitems.listeners.EntityDamageListener;
import com.jvmitems.listeners.PlayerInteractListener;
import com.jvmitems.listeners.ProjectileEffectListener;
import com.jvmitems.util.ColorUtil;
import com.jvmitems.util.CooldownManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class JVMItems extends JavaPlugin {

    private static JVMItems instance;

    private ItemManager itemManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        itemManager = new ItemManager(this);
        itemManager.load();

        cooldownManager = new CooldownManager();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new ProjectileEffectListener(this), this);
        pm.registerEvents(new EntityDamageListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new AntiGriefListener(this), this);

        JVMItemsCommand command = new JVMItemsCommand(this);
        getCommand("jvmitems").setExecutor(command);
        getCommand("jvmitems").setTabCompleter(new JVMItemsTabCompleter(this));

        getLogger().info("JVMItems habilitado (" + itemManager.getItems().size() + " itens carregados). NMS: "
                + com.jvmitems.compat.VersionUtil.nmsVersion());
    }

    @Override
    public void onDisable() {
        if (itemManager != null) {
            itemManager.save();
        }
    }

    public void reload() {
        reloadConfig();
        itemManager.load();
    }

    public static JVMItems getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public String message(String key) {
        FileConfiguration config = getConfig();
        String prefix = ColorUtil.colorize(config.getString("prefix", ""));
        String message = config.getString("messages." + key, key);
        return prefix + ColorUtil.colorize(message);
    }

    public String message(String key, String... placeholdersAndValues) {
        String message = message(key);
        for (int i = 0; i + 1 < placeholdersAndValues.length; i += 2) {
            message = message.replace("%" + placeholdersAndValues[i] + "%", placeholdersAndValues[i + 1]);
        }
        return message;
    }

    public int defaultCooldown() {
        return getConfig().getInt("default-cooldown", 3);
    }

    public boolean antiGrief() {
        return getConfig().getBoolean("anti-grief", true);
    }

    public boolean allowProjectilePickup() {
        return getConfig().getBoolean("projectiles.allow-pickup", false);
    }

    public boolean removeProjectileOnHit() {
        return getConfig().getBoolean("projectiles.remove-on-hit", true);
    }

    public int projectileLifetimeSeconds() {
        return Math.max(0, getConfig().getInt("projectiles.lifetime-seconds", 20));
    }
}
