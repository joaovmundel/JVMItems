package com.jvmitems.compat;

import org.bukkit.inventory.ItemStack;

public interface AttributeApplier {

    ItemStack applyAttributes(ItemStack item, Double attackDamage, Double armor, Double armorToughness);

    static AttributeApplier create() {
        return VersionUtil.isLegacyAttributeApi() ? new Legacy12AttributeApplier() : new ModernAttributeApplier();
    }
}
