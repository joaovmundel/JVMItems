package com.jvmitems.item;

import com.jvmitems.compat.AttributeApplier;
import com.jvmitems.compat.ItemTagStorage;
import com.jvmitems.power.Power;
import com.jvmitems.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public class ItemManager {

    private final Plugin plugin;
    private final File file;
    private final ItemTagStorage tagStorage;
    private final AttributeApplier attributeApplier;
    private final Map<String, CustomItem> items = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public ItemManager(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "items.yml");
        this.tagStorage = ItemTagStorage.create(plugin);
        this.attributeApplier = AttributeApplier.create();
    }

    public void load() {
        items.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection == null) {
            return;
        }
        for (String id : itemsSection.getKeys(false)) {
            try {
                ConfigurationSection section = itemsSection.getConfigurationSection(id);
                CustomItem item = loadItem(id, section);
                items.put(id, item);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Falha ao carregar o item '" + id + "' de items.yml", e);
            }
        }
    }

    private CustomItem loadItem(String id, ConfigurationSection section) {
        Material material = Material.valueOf(section.getString("material", "STICK").toUpperCase());
        CustomItem item = new CustomItem(id, material);
        item.setDisplayName(section.getString("display-name", material.name()));
        item.setLore(section.getStringList("lore"));
        item.setUnbreakable(section.getBoolean("unbreakable", false));

        if (section.contains("attack-damage")) {
            item.setAttackDamage(section.getDouble("attack-damage"));
        }
        if (section.contains("armor")) {
            item.setArmor(section.getDouble("armor"));
        }
        if (section.contains("armor-toughness")) {
            item.setArmorToughness(section.getDouble("armor-toughness"));
        }

        ConfigurationSection enchantSection = section.getConfigurationSection("enchantments");
        if (enchantSection != null) {
            for (String enchantName : enchantSection.getKeys(false)) {
                item.getEnchantments().put(enchantName.toUpperCase(), enchantSection.getInt(enchantName));
            }
        }

        java.util.List<java.util.Map<?, ?>> rawPowers = section.getMapList("powers");
        for (java.util.Map<?, ?> raw : rawPowers) {
            YamlConfiguration temp = new YamlConfiguration();
            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                temp.set(String.valueOf(entry.getKey()), entry.getValue());
            }
            try {
                item.getPowers().add(Power.load(temp));
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Falha ao carregar um power do item '" + id + "'", e);
            }
        }

        return item;
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection itemsSection = config.createSection("items");
        for (CustomItem item : items.values()) {
            ConfigurationSection section = itemsSection.createSection(item.getId());
            section.set("material", item.getMaterial().name());
            section.set("display-name", item.getDisplayName());
            section.set("lore", item.getLore());
            section.set("unbreakable", item.isUnbreakable());
            if (item.getAttackDamage() != null) {
                section.set("attack-damage", item.getAttackDamage());
            }
            if (item.getArmor() != null) {
                section.set("armor", item.getArmor());
            }
            if (item.getArmorToughness() != null) {
                section.set("armor-toughness", item.getArmorToughness());
            }
            if (!item.getEnchantments().isEmpty()) {
                ConfigurationSection enchantSection = section.createSection("enchantments");
                for (Map.Entry<String, Integer> entry : item.getEnchantments().entrySet()) {
                    enchantSection.set(entry.getKey(), entry.getValue());
                }
            }
            if (!item.getPowers().isEmpty()) {
                java.util.List<Map<String, Object>> powerMaps = new java.util.ArrayList<>();
                for (Power power : item.getPowers()) {
                    YamlConfiguration temp = new YamlConfiguration();
                    power.save(temp);
                    Map<String, Object> map = new LinkedHashMap<>();
                    for (String key : temp.getKeys(false)) {
                        map.put(key, temp.get(key));
                    }
                    powerMaps.add(map);
                }
                section.set("powers", powerMaps);
            }
        }

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao salvar items.yml", e);
        }
    }

    public Map<String, CustomItem> getItems() {
        return items;
    }

    public CustomItem get(String id) {
        return items.get(id);
    }

    public boolean exists(String id) {
        return items.containsKey(id);
    }

    public void add(CustomItem item) {
        items.put(item.getId(), item);
    }

    public void remove(String id) {
        items.remove(id);
    }

    public ItemTagStorage getTagStorage() {
        return tagStorage;
    }

    public String identify(ItemStack stack) {
        return tagStorage.getId(stack);
    }

    public ItemStack build(CustomItem item) {
        ItemStack stack = new ItemStack(item.getMaterial());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.colorize(item.getDisplayName()));
            if (!item.getLore().isEmpty()) {
                meta.setLore(ColorUtil.colorize(item.getLore()));
            }
            meta.setUnbreakable(item.isUnbreakable());
            try {
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
            } catch (NoSuchFieldError | Exception ignored) {
                // alguns ItemFlag podem nao existir em versoes muito antigas; ignora
            }
            for (Map.Entry<String, Integer> entry : item.getEnchantments().entrySet()) {
                Enchantment enchantment = Enchantment.getByName(entry.getKey());
                if (enchantment != null) {
                    meta.addEnchant(enchantment, entry.getValue(), true);
                }
            }
            stack.setItemMeta(meta);
        }

        if (item.getAttackDamage() != null || item.getArmor() != null || item.getArmorToughness() != null) {
            stack = attributeApplier.applyAttributes(stack, item.getAttackDamage(), item.getArmor(), item.getArmorToughness());
        }

        stack = tagStorage.setId(stack, item.getId());
        return stack;
    }
}
