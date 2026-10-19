package com.jvmitems.compat;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ModernAttributeApplier implements AttributeApplier {

    private static final String ATTACK_DAMAGE_NAME = "jvmitems.attack_damage";
    private static final String ARMOR_NAME = "jvmitems.armor";
    private static final String ARMOR_TOUGHNESS_NAME = "jvmitems.armor_toughness";

    @Override
    public ItemStack applyAttributes(ItemStack item, Double attackDamage, Double armor, Double armorToughness) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        removeExisting(meta, Attribute.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE_NAME);
        removeExisting(meta, Attribute.GENERIC_ARMOR, ARMOR_NAME);
        removeExisting(meta, Attribute.GENERIC_ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_NAME);

        if (attackDamage != null) {
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier(ATTACK_DAMAGE_NAME, attackDamage));
        }
        if (armor != null) {
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier(ARMOR_NAME, armor));
        }
        if (armorToughness != null) {
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier(ARMOR_TOUGHNESS_NAME, armorToughness));
        }

        item.setItemMeta(meta);
        return item;
    }

    private void removeExisting(ItemMeta meta, Attribute attribute, String name) {
        if (meta.getAttributeModifiers() == null || meta.getAttributeModifiers(attribute) == null) {
            return;
        }
        List<AttributeModifier> toRemove = new ArrayList<>(meta.getAttributeModifiers(attribute));
        for (AttributeModifier modifier : toRemove) {
            if (modifier.getName().equals(name)) {
                meta.removeAttributeModifier(attribute, modifier);
            }
        }
    }

    private AttributeModifier modifier(String name, double amount) {
        return new AttributeModifier(UUID.randomUUID(), name, amount, AttributeModifier.Operation.ADD_NUMBER);
    }
}
