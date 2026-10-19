package com.jvmitems.compat;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

class Legacy12AttributeApplier implements AttributeApplier {

    private static final Logger LOGGER = Logger.getLogger("JVMItems");

    @Override
    public ItemStack applyAttributes(ItemStack item, Double attackDamage, Double armor, Double armorToughness) {
        try {
            Class<?> craftItemStackClass = ReflectionUtil.craftClass("inventory.CraftItemStack");
            Class<?> nmsItemStackClass = ReflectionUtil.nmsClass("ItemStack");
            Class<?> nbtTagCompoundClass = ReflectionUtil.nmsClass("NBTTagCompound");
            Class<?> nbtTagListClass = ReflectionUtil.nmsClass("NBTTagList");
            Class<?> nbtBaseClass = ReflectionUtil.nmsClass("NBTBase");

            Object nmsItem = ReflectionUtil.invokeStatic(craftItemStackClass, "asNMSCopy",
                    new Class[]{ItemStack.class}, item);
            if (nmsItem == null) {
                return item;
            }

            Object tag = ReflectionUtil.invoke(nmsItem, "getTag");
            if (tag == null) {
                tag = ReflectionUtil.newInstance(nbtTagCompoundClass);
                ReflectionUtil.invoke(nmsItem, "setTag", new Class[]{nbtTagCompoundClass}, tag);
            }

            Object list = ReflectionUtil.newInstance(nbtTagListClass);

            if (attackDamage != null) {
                addModifier(list, nbtTagCompoundClass, nbtBaseClass, "generic.attackDamage",
                        "JVMItemsAttackDamage", attackDamage);
            }
            if (armor != null) {
                addModifier(list, nbtTagCompoundClass, nbtBaseClass, "generic.armor",
                        "JVMItemsArmor", armor);
            }
            if (armorToughness != null) {
                addModifier(list, nbtTagCompoundClass, nbtBaseClass, "generic.armorToughness",
                        "JVMItemsArmorToughness", armorToughness);
            }

            ReflectionUtil.invoke(tag, "set", new Class[]{String.class, nbtBaseClass},
                    "AttributeModifiers", list);

            Object bukkitItem = ReflectionUtil.invokeStatic(craftItemStackClass, "asBukkitCopy",
                    new Class[]{nmsItemStackClass}, nmsItem);
            return (ItemStack) bukkitItem;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Nao foi possivel aplicar atributos via NBT (1.12)", e);
            return item;
        }
    }

    private void addModifier(Object list, Class<?> nbtTagCompoundClass, Class<?> nbtBaseClass,
                              String attributeName, String modifierName, double amount) throws Exception {
        Object entry = ReflectionUtil.newInstance(nbtTagCompoundClass);
        UUID uuid = UUID.randomUUID();

        ReflectionUtil.invoke(entry, "setString", new Class[]{String.class, String.class},
                "AttributeName", attributeName);
        ReflectionUtil.invoke(entry, "setString", new Class[]{String.class, String.class},
                "Name", modifierName);
        ReflectionUtil.invoke(entry, "setDouble", new Class[]{String.class, double.class},
                "Amount", amount);
        ReflectionUtil.invoke(entry, "setInt", new Class[]{String.class, int.class},
                "Operation", 0);
        ReflectionUtil.invoke(entry, "setLong", new Class[]{String.class, long.class},
                "UUIDMost", uuid.getMostSignificantBits());
        ReflectionUtil.invoke(entry, "setLong", new Class[]{String.class, long.class},
                "UUIDLeast", uuid.getLeastSignificantBits());

        ReflectionUtil.invoke(list, "add", new Class[]{nbtBaseClass}, entry);
    }
}
