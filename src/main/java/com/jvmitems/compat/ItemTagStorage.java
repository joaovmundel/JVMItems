package com.jvmitems.compat;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface ItemTagStorage {

    String getId(ItemStack item);

    ItemStack setId(ItemStack item, String id);

    static ItemTagStorage create(Plugin plugin) {
        return VersionUtil.hasPersistentDataContainer() ? new PdcItemTagStorage(plugin) : new LoreItemTagStorage();
    }
}
