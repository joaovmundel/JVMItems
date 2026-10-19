package com.jvmitems.item;

import com.jvmitems.power.Power;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomItem {

    private final String id;
    private Material material;
    private String displayName;
    private final List<String> lore = new ArrayList<>();
    private final Map<String, Integer> enchantments = new LinkedHashMap<>();
    private boolean unbreakable = false;

    private Double attackDamage;
    private Double armor;
    private Double armorToughness;

    private final List<Power> powers = new ArrayList<>();

    public CustomItem(String id, Material material) {
        this.id = id;
        this.material = material;
        this.displayName = material.name();
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> newLore) {
        lore.clear();
        lore.addAll(newLore);
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public Double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(Double attackDamage) {
        this.attackDamage = attackDamage;
    }

    public Double getArmor() {
        return armor;
    }

    public void setArmor(Double armor) {
        this.armor = armor;
    }

    public Double getArmorToughness() {
        return armorToughness;
    }

    public void setArmorToughness(Double armorToughness) {
        this.armorToughness = armorToughness;
    }

    public List<Power> getPowers() {
        return powers;
    }
}
