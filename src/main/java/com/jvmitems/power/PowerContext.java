package com.jvmitems.power;

import com.jvmitems.item.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class PowerContext {

    private final Player user;
    private final ItemStack item;
    private final CustomItem customItem;
    private final Trigger trigger;

    private LivingEntity opponent;
    private Block block;
    private EntityDamageByEntityEvent damageEvent;
    private double damageAmount;

    public PowerContext(Player user, ItemStack item, CustomItem customItem, Trigger trigger) {
        this.user = user;
        this.item = item;
        this.customItem = customItem;
        this.trigger = trigger;
    }

    public Player getUser() {
        return user;
    }

    public ItemStack getItem() {
        return item;
    }

    public CustomItem getCustomItem() {
        return customItem;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public LivingEntity getOpponent() {
        return opponent;
    }

    public PowerContext opponent(LivingEntity opponent) {
        this.opponent = opponent;
        return this;
    }

    public Block getBlock() {
        return block;
    }

    public PowerContext block(Block block) {
        this.block = block;
        return this;
    }

    public EntityDamageByEntityEvent getDamageEvent() {
        return damageEvent;
    }

    public PowerContext damageEvent(EntityDamageByEntityEvent damageEvent) {
        this.damageEvent = damageEvent;
        return this;
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    public PowerContext damageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
        return this;
    }

    public Location resolveLocation() {
        if (opponent != null) {
            return opponent.getLocation();
        }
        if (block != null) {
            return block.getLocation().add(0.5, 0.5, 0.5);
        }
        Block target = user.getTargetBlock(Collections.singleton(Material.AIR), 30);
        if (target != null && target.getType() != Material.AIR) {
            return target.getLocation().add(0.5, 0.5, 0.5);
        }
        Location eye = user.getEyeLocation();
        return eye.add(eye.getDirection().multiply(5));
    }
}
