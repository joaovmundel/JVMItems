package com.jvmitems.commands;

import com.jvmitems.JVMItems;
import com.jvmitems.item.CustomItem;
import com.jvmitems.item.ItemManager;
import com.jvmitems.power.Condition;
import com.jvmitems.power.Power;
import com.jvmitems.power.PowerType;
import com.jvmitems.power.Trigger;
import com.jvmitems.power.impl.AoeDamagePower;
import com.jvmitems.power.impl.CommandPower;
import com.jvmitems.power.impl.DamageMultiplierPower;
import com.jvmitems.power.impl.FireworkPower;
import com.jvmitems.power.impl.HealPower;
import com.jvmitems.power.impl.KnockbackPower;
import com.jvmitems.power.impl.LifestealPower;
import com.jvmitems.power.impl.LightningPower;
import com.jvmitems.power.impl.PotionEffectPower;
import com.jvmitems.power.impl.ProjectilePower;
import com.jvmitems.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class JVMItemsCommand implements CommandExecutor {

    private final JVMItems plugin;

    public JVMItemsCommand(JVMItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            HelpMenu.send(sender, label, new String[0]);
            return true;
        }

        String sub = args[0].toLowerCase();
        String[] rest = java.util.Arrays.copyOfRange(args, 1, args.length);

        switch (sub) {
            case "help":
            case "?":
                HelpMenu.send(sender, label, rest);
                return true;
            case "list":
                return requirePerm(sender, "jvmitems.admin") && handleList(sender);
            case "info":
                return requirePerm(sender, "jvmitems.admin") && handleInfo(sender, rest);
            case "create":
                return requirePerm(sender, "jvmitems.admin") && handleCreate(sender, rest);
            case "delete":
                return requirePerm(sender, "jvmitems.admin") && handleDelete(sender, rest);
            case "give":
                return requirePerm(sender, "jvmitems.give") && handleGive(sender, rest);
            case "setname":
                return requirePerm(sender, "jvmitems.admin") && handleSetName(sender, rest);
            case "addlore":
                return requirePerm(sender, "jvmitems.admin") && handleAddLore(sender, rest);
            case "clearlore":
                return requirePerm(sender, "jvmitems.admin") && handleClearLore(sender, rest);
            case "setmaterial":
                return requirePerm(sender, "jvmitems.admin") && handleSetMaterial(sender, rest);
            case "setdamage":
                return requirePerm(sender, "jvmitems.admin") && handleSetDamage(sender, rest);
            case "setarmor":
                return requirePerm(sender, "jvmitems.admin") && handleSetArmor(sender, rest);
            case "setarmortoughness":
                return requirePerm(sender, "jvmitems.admin") && handleSetArmorToughness(sender, rest);
            case "setunbreakable":
                return requirePerm(sender, "jvmitems.admin") && handleSetUnbreakable(sender, rest);
            case "enchant":
                return requirePerm(sender, "jvmitems.admin") && handleEnchant(sender, rest);
            case "power":
                return requirePerm(sender, "jvmitems.admin") && handlePower(sender, rest);
            case "reload":
                return requirePerm(sender, "jvmitems.admin") && handleReload(sender);
            default:
                sender.sendMessage(ColorUtil.colorize("&cSubcomando desconhecido: &f" + args[0]));
                HelpMenu.send(sender, label, new String[0]);
                return true;
        }
    }

    private boolean requirePerm(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(plugin.message("no-permission"));
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------- basics

    private boolean handleList(CommandSender sender) {
        ItemManager manager = plugin.getItemManager();
        if (manager.getItems().isEmpty()) {
            sender.sendMessage(ColorUtil.colorize("&7Nenhum item customizado foi criado ainda."));
            return true;
        }
        sender.sendMessage(ColorUtil.colorize("&6Itens customizados (&e" + manager.getItems().size() + "&6):"));
        for (String id : manager.getItems().keySet()) {
            sender.sendMessage(ColorUtil.colorize("&7 - &f" + id));
        }
        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems info <id>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        sender.sendMessage(ColorUtil.colorize("&6&l=== " + item.getId() + " ==="));
        sender.sendMessage(ColorUtil.colorize("&7Material: &f" + item.getMaterial()));
        sender.sendMessage(ColorUtil.colorize("&7Nome: " + item.getDisplayName()));
        sender.sendMessage(ColorUtil.colorize("&7Dano: &f" + item.getAttackDamage()));
        sender.sendMessage(ColorUtil.colorize("&7Armadura: &f" + item.getArmor()));
        sender.sendMessage(ColorUtil.colorize("&7Toughness: &f" + item.getArmorToughness()));
        sender.sendMessage(ColorUtil.colorize("&7Unbreakable: &f" + item.isUnbreakable()));
        sender.sendMessage(ColorUtil.colorize("&7Encantamentos: &f" + item.getEnchantments()));
        sender.sendMessage(ColorUtil.colorize("&7Powers: &f" + item.getPowers().size()
                + " &7(use /jvmitems power list " + item.getId() + " para detalhes)"));
        return true;
    }

    private boolean handleCreate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems create <id> <material>"));
            return true;
        }
        String id = args[0];
        if (plugin.getItemManager().exists(id)) {
            sender.sendMessage(plugin.message("item-already-exists", "id", id));
            return true;
        }
        Material material = parseMaterial(sender, args[1]);
        if (material == null) {
            return true;
        }
        CustomItem item = new CustomItem(id, material);
        plugin.getItemManager().add(item);
        plugin.getItemManager().save();
        sender.sendMessage(plugin.message("item-created", "id", id));
        return true;
    }

    private boolean handleDelete(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems delete <id>"));
            return true;
        }
        if (requireItem(sender, args[0]) == null) {
            return true;
        }
        plugin.getItemManager().remove(args[0]);
        plugin.getItemManager().save();
        sender.sendMessage(plugin.message("item-deleted", "id", args[0]));
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems give <id> [jogador] [quantidade]"));
            return true;
        }
        CustomItem customItem = requireItem(sender, args[0]);
        if (customItem == null) {
            return true;
        }

        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ColorUtil.colorize("&cJogador '" + args[1] + "' nao encontrado ou offline."));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ColorUtil.colorize("&cVoce precisa informar um jogador quando usar o console."));
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            Integer parsed = parseInt(sender, args[2]);
            if (parsed == null) {
                return true;
            }
            amount = Math.max(1, parsed);
        }

        ItemStack stack = plugin.getItemManager().build(customItem);
        stack.setAmount(amount);
        java.util.Map<Integer, ItemStack> overflow = target.getInventory().addItem(stack);
        for (ItemStack extra : overflow.values()) {
            target.getWorld().dropItem(target.getLocation(), extra);
        }

        target.sendMessage(plugin.message("item-received", "id", customItem.getId()));
        if (!target.equals(sender)) {
            sender.sendMessage(plugin.message("item-given", "id", customItem.getId(), "player", target.getName()));
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.reload();
        sender.sendMessage(plugin.message("reloaded"));
        return true;
    }

    // --------------------------------------------------------- aparencia

    private boolean handleSetName(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems setname <id> <nome>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        String name = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        item.setDisplayName(name);
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aNome de '" + item.getId() + "' atualizado."));
        return true;
    }

    private boolean handleAddLore(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems addlore <id> <linha>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        String line = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        List<String> lore = new ArrayList<>(item.getLore());
        lore.add(line);
        item.setLore(lore);
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aLinha de lore adicionada em '" + item.getId() + "'."));
        return true;
    }

    private boolean handleClearLore(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems clearlore <id>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        item.setLore(new ArrayList<>());
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aLore de '" + item.getId() + "' limpa."));
        return true;
    }

    private boolean handleSetMaterial(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems setmaterial <id> <material>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        Material material = parseMaterial(sender, args[1]);
        if (material == null) {
            return true;
        }
        item.setMaterial(material);
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aMaterial de '" + item.getId() + "' atualizado."));
        return true;
    }

    // ------------------------------------------------------- atributos

    private boolean handleSetDamage(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems setdamage <id> <valor|none>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        if (args[1].equalsIgnoreCase("none")) {
            item.setAttackDamage(null);
        } else {
            Double value = parseDouble(sender, args[1]);
            if (value == null) {
                return true;
            }
            item.setAttackDamage(value);
        }
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aDano de '" + item.getId() + "' atualizado."));
        return true;
    }

    private boolean handleSetArmor(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems setarmor <id> <valor|none>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        if (args[1].equalsIgnoreCase("none")) {
            item.setArmor(null);
        } else {
            Double value = parseDouble(sender, args[1]);
            if (value == null) {
                return true;
            }
            item.setArmor(value);
        }
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aArmadura de '" + item.getId() + "' atualizada."));
        return true;
    }

    private boolean handleSetArmorToughness(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems setarmortoughness <id> <valor|none>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        if (args[1].equalsIgnoreCase("none")) {
            item.setArmorToughness(null);
        } else {
            Double value = parseDouble(sender, args[1]);
            if (value == null) {
                return true;
            }
            item.setArmorToughness(value);
        }
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aArmor toughness de '" + item.getId() + "' atualizada."));
        return true;
    }

    private boolean handleSetUnbreakable(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems setunbreakable <id> <true|false>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        item.setUnbreakable(Boolean.parseBoolean(args[1]));
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aUnbreakable de '" + item.getId() + "' atualizado."));
        return true;
    }

    private boolean handleEnchant(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems enchant add|remove <id> <encantamento> [nivel]"));
            return true;
        }
        String action = args[0].toLowerCase();
        CustomItem item = requireItem(sender, args[1]);
        if (item == null) {
            return true;
        }
        String enchantName = args[2].toUpperCase();
        Enchantment enchantment = Enchantment.getByName(enchantName);
        if (enchantment == null) {
            sender.sendMessage(plugin.message("invalid-enchantment", "enchant", args[2]));
            return true;
        }
        if (action.equals("add")) {
            int level = 1;
            if (args.length >= 4) {
                Integer parsed = parseInt(sender, args[3]);
                if (parsed == null) {
                    return true;
                }
                level = parsed;
            }
            item.getEnchantments().put(enchantName, level);
            sender.sendMessage(ColorUtil.colorize("&aEncantamento " + enchantName + " " + level + " adicionado em '" + item.getId() + "'."));
        } else if (action.equals("remove")) {
            item.getEnchantments().remove(enchantName);
            sender.sendMessage(ColorUtil.colorize("&aEncantamento " + enchantName + " removido de '" + item.getId() + "'."));
        } else {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems enchant add|remove <id> <encantamento> [nivel]"));
            return true;
        }
        plugin.getItemManager().save();
        return true;
    }

    // ------------------------------------------------------------- powers

    private boolean handlePower(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power <add|remove|list|setfireball|condition> ..."));
            return true;
        }
        String action = args[0].toLowerCase();
        String[] rest = java.util.Arrays.copyOfRange(args, 1, args.length);

        switch (action) {
            case "add":
                return handlePowerAdd(sender, rest);
            case "remove":
                return handlePowerRemove(sender, rest);
            case "list":
                return handlePowerList(sender, rest);
            case "setfireball":
                return handlePowerSetFireball(sender, rest);
            case "condition":
                return handlePowerCondition(sender, rest);
            default:
                sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power <add|remove|list|setfireball|condition> ..."));
                return true;
        }
    }

    private boolean handlePowerAdd(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power add <tipo> <id> <trigger> <cooldown> [args...]"));
            return true;
        }
        String typeName = args[0].equalsIgnoreCase("ice") ? "snowball" : args[0].toLowerCase();
        PowerType type;
        try {
            type = PowerType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ColorUtil.colorize("&cTipo de power invalido: " + args[0]));
            return true;
        }

        CustomItem item = requireItem(sender, args[1]);
        if (item == null) {
            return true;
        }

        Trigger trigger;
        try {
            trigger = Trigger.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ColorUtil.colorize("&cTrigger invalido: " + args[2]));
            return true;
        }

        Integer cooldown = parseInt(sender, args[3]);
        if (cooldown == null) {
            return true;
        }

        String[] extra = java.util.Arrays.copyOfRange(args, 4, args.length);
        Power power = buildPower(sender, type, trigger, cooldown, extra);
        if (power == null) {
            return true;
        }

        item.getPowers().add(power);
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aPower " + type + " adicionado em '" + item.getId()
                + "' (indice " + (item.getPowers().size() - 1) + ")."));
        return true;
    }

    private Power buildPower(CommandSender sender, PowerType type, Trigger trigger, int cooldown, String[] extra) {
        switch (type) {
            case ARROW:
            case FIREBALL:
            case SNOWBALL: {
                ProjectilePower power = new ProjectilePower(plugin, type, trigger, cooldown);
                if (extra.length >= 1) {
                    Integer amount = parseInt(sender, extra[0]);
                    if (amount == null) {
                        return null;
                    }
                    power.setAmount(amount);
                }
                if (extra.length >= 2) {
                    Double speed = parseDouble(sender, extra[1]);
                    if (speed == null) {
                        return null;
                    }
                    power.setSpeed(speed);
                }
                if (extra.length >= 3) {
                    Double spread = parseDouble(sender, extra[2]);
                    if (spread == null) {
                        return null;
                    }
                    power.setSpread(spread);
                }
                return power;
            }
            case POTION: {
                if (extra.length < 4) {
                    sender.sendMessage(plugin.message("usage", "usage",
                            "/jvmitems power add potion <id> <trigger> <cooldown> <self|opponent> <pocao> <duracaoTicks> <amplificador>"));
                    return null;
                }
                PotionEffectPower.Target target;
                try {
                    target = PotionEffectPower.Target.valueOf(extra[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ColorUtil.colorize("&cAlvo invalido (use self ou opponent): " + extra[0]));
                    return null;
                }
                PotionEffectType potionType = PotionEffectType.getByName(extra[1].toUpperCase());
                if (potionType == null) {
                    sender.sendMessage(plugin.message("invalid-potion", "potion", extra[1]));
                    return null;
                }
                Integer duration = parseInt(sender, extra[2]);
                Integer amplifier = parseInt(sender, extra[3]);
                if (duration == null || amplifier == null) {
                    return null;
                }
                PotionEffectPower power = new PotionEffectPower(trigger, cooldown);
                power.setTarget(target);
                power.setPotionType(potionType);
                power.setDurationTicks(duration);
                power.setAmplifier(amplifier);
                return power;
            }
            case LIFESTEAL: {
                if (extra.length < 1) {
                    sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power add lifesteal <id> <trigger> <cooldown> <percentual 0-1>"));
                    return null;
                }
                Double percent = parseDouble(sender, extra[0]);
                if (percent == null) {
                    return null;
                }
                LifestealPower power = new LifestealPower(trigger, cooldown);
                power.setPercent(percent);
                return power;
            }
            case DAMAGE_MULTIPLIER: {
                if (extra.length < 1) {
                    sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power add damagemultiplier <id> <trigger> <cooldown> <multiplicador>"));
                    return null;
                }
                Double multiplier = parseDouble(sender, extra[0]);
                if (multiplier == null) {
                    return null;
                }
                DamageMultiplierPower power = new DamageMultiplierPower(trigger, cooldown);
                power.setMultiplier(multiplier);
                return power;
            }
            case KNOCKBACK: {
                if (extra.length < 1) {
                    sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power add knockback <id> <trigger> <cooldown> <forca>"));
                    return null;
                }
                Double strength = parseDouble(sender, extra[0]);
                if (strength == null) {
                    return null;
                }
                KnockbackPower power = new KnockbackPower(trigger, cooldown);
                power.setStrength(strength);
                return power;
            }
            case LIGHTNING: {
                boolean damaging = extra.length < 1 || Boolean.parseBoolean(extra[0]);
                LightningPower power = new LightningPower(trigger, cooldown);
                power.setDamaging(damaging);
                return power;
            }
            case HEAL: {
                if (extra.length < 1) {
                    sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power add heal <id> <trigger> <cooldown> <quantidade>"));
                    return null;
                }
                Double amount = parseDouble(sender, extra[0]);
                if (amount == null) {
                    return null;
                }
                HealPower power = new HealPower(trigger, cooldown);
                power.setAmount(amount);
                return power;
            }
            case FIREWORK: {
                int fireworkPower = 1;
                if (extra.length >= 1) {
                    Integer parsed = parseInt(sender, extra[0]);
                    if (parsed == null) {
                        return null;
                    }
                    fireworkPower = parsed;
                }
                FireworkPower power = new FireworkPower(trigger, cooldown);
                power.setPower(fireworkPower);
                return power;
            }
            case AOE_DAMAGE: {
                if (extra.length < 2) {
                    sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power add aoedamage <id> <trigger> <cooldown> <raio> <dano>"));
                    return null;
                }
                Double radius = parseDouble(sender, extra[0]);
                Double damage = parseDouble(sender, extra[1]);
                if (radius == null || damage == null) {
                    return null;
                }
                AoeDamagePower power = new AoeDamagePower(trigger, cooldown);
                power.setRadius(radius);
                power.setDamage(damage);
                return power;
            }
            case COMMAND: {
                if (extra.length < 2) {
                    sender.sendMessage(plugin.message("usage", "usage",
                            "/jvmitems power add command <id> <trigger> <cooldown> <console:true|false> <comando>"));
                    return null;
                }
                boolean console = Boolean.parseBoolean(extra[0]);
                String cmd = String.join(" ", java.util.Arrays.copyOfRange(extra, 1, extra.length));
                CommandPower power = new CommandPower(trigger, cooldown);
                power.setConsole(console);
                power.setCommand(cmd);
                return power;
            }
            default:
                sender.sendMessage(ColorUtil.colorize("&cTipo de power nao suportado: " + type));
                return null;
        }
    }

    private boolean handlePowerRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power remove <id> <indice>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        Integer index = parseInt(sender, args[1]);
        if (index == null || index < 0 || index >= item.getPowers().size()) {
            sender.sendMessage(ColorUtil.colorize("&cIndice invalido."));
            return true;
        }
        item.getPowers().remove((int) index);
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aPower removido de '" + item.getId() + "'."));
        return true;
    }

    private boolean handlePowerList(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power list <id>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        if (item.getPowers().isEmpty()) {
            sender.sendMessage(ColorUtil.colorize("&7'" + item.getId() + "' nao tem powers."));
            return true;
        }
        for (int i = 0; i < item.getPowers().size(); i++) {
            sender.sendMessage(ColorUtil.colorize("&7[" + i + "] &f" + item.getPowers().get(i).describe()));
        }
        return true;
    }

    private boolean handlePowerSetFireball(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power setfireball <id> <indice> <incendiary> <explosive>"));
            return true;
        }
        CustomItem item = requireItem(sender, args[0]);
        if (item == null) {
            return true;
        }
        Integer index = parseInt(sender, args[1]);
        if (index == null || index < 0 || index >= item.getPowers().size()) {
            sender.sendMessage(ColorUtil.colorize("&cIndice invalido."));
            return true;
        }
        Power power = item.getPowers().get(index);
        if (!(power instanceof ProjectilePower) || power.getType() != PowerType.FIREBALL) {
            sender.sendMessage(ColorUtil.colorize("&cEsse power nao e do tipo fireball."));
            return true;
        }
        ProjectilePower projectilePower = (ProjectilePower) power;
        projectilePower.setIncendiary(Boolean.parseBoolean(args[2]));
        if (args.length >= 4) {
            projectilePower.setExplosive(Boolean.parseBoolean(args[3]));
        }
        plugin.getItemManager().save();
        sender.sendMessage(ColorUtil.colorize("&aConfiguracoes da fireball de '" + item.getId() + "' atualizadas."));
        return true;
    }

    private boolean handlePowerCondition(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power condition <add|remove> <id> <indice> ..."));
            return true;
        }
        String action = args[0].toLowerCase();
        String[] rest = java.util.Arrays.copyOfRange(args, 1, args.length);

        if (action.equals("add")) {
            if (rest.length < 3) {
                sender.sendMessage(plugin.message("usage", "usage",
                        "/jvmitems power condition add <id> <indice> <permission|sneaking|notsneaking|healthbelow|healthabove|chance> [valor]"));
                return true;
            }
            CustomItem item = requireItem(sender, rest[0]);
            if (item == null) {
                return true;
            }
            Integer index = parseInt(sender, rest[1]);
            if (index == null || index < 0 || index >= item.getPowers().size()) {
                sender.sendMessage(ColorUtil.colorize("&cIndice invalido."));
                return true;
            }
            String conditionType = rest[2].toLowerCase();
            Condition condition;
            switch (conditionType) {
                case "permission":
                    if (rest.length < 4) {
                        sender.sendMessage(ColorUtil.colorize("&cInforme a permissao."));
                        return true;
                    }
                    condition = Condition.permission(rest[3]);
                    break;
                case "sneaking":
                    condition = Condition.sneaking(true);
                    break;
                case "notsneaking":
                    condition = Condition.sneaking(false);
                    break;
                case "healthbelow": {
                    if (rest.length < 4) {
                        sender.sendMessage(ColorUtil.colorize("&cInforme o percentual (0-1)."));
                        return true;
                    }
                    Double value = parseDouble(sender, rest[3]);
                    if (value == null) {
                        return true;
                    }
                    condition = Condition.healthBelow(value);
                    break;
                }
                case "healthabove": {
                    if (rest.length < 4) {
                        sender.sendMessage(ColorUtil.colorize("&cInforme o percentual (0-1)."));
                        return true;
                    }
                    Double value = parseDouble(sender, rest[3]);
                    if (value == null) {
                        return true;
                    }
                    condition = Condition.healthAbove(value);
                    break;
                }
                case "chance": {
                    if (rest.length < 4) {
                        sender.sendMessage(ColorUtil.colorize("&cInforme a chance (0-1)."));
                        return true;
                    }
                    Double value = parseDouble(sender, rest[3]);
                    if (value == null) {
                        return true;
                    }
                    condition = Condition.chance(value);
                    break;
                }
                default:
                    sender.sendMessage(ColorUtil.colorize("&cTipo de condicao invalido: " + conditionType));
                    return true;
            }

            item.getPowers().get(index).getConditions().add(condition);
            plugin.getItemManager().save();
            sender.sendMessage(ColorUtil.colorize("&aCondicao adicionada ao power " + index + " de '" + item.getId() + "'."));
            return true;
        }

        if (action.equals("remove")) {
            if (rest.length < 3) {
                sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power condition remove <id> <indice> <indiceCondicao>"));
                return true;
            }
            CustomItem item = requireItem(sender, rest[0]);
            if (item == null) {
                return true;
            }
            Integer index = parseInt(sender, rest[1]);
            if (index == null || index < 0 || index >= item.getPowers().size()) {
                sender.sendMessage(ColorUtil.colorize("&cIndice invalido."));
                return true;
            }
            Power power = item.getPowers().get(index);
            Integer conditionIndex = parseInt(sender, rest[2]);
            if (conditionIndex == null || conditionIndex < 0 || conditionIndex >= power.getConditions().size()) {
                sender.sendMessage(ColorUtil.colorize("&cIndice de condicao invalido."));
                return true;
            }
            power.getConditions().remove((int) conditionIndex);
            plugin.getItemManager().save();
            sender.sendMessage(ColorUtil.colorize("&aCondicao removida."));
            return true;
        }

        sender.sendMessage(plugin.message("usage", "usage", "/jvmitems power condition <add|remove> <id> <indice> ..."));
        return true;
    }

    // ----------------------------------------------------------- helpers

    private CustomItem requireItem(CommandSender sender, String id) {
        CustomItem item = plugin.getItemManager().get(id);
        if (item == null) {
            sender.sendMessage(plugin.message("item-not-found", "id", id));
        }
        return item;
    }

    private Material parseMaterial(CommandSender sender, String name) {
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.message("invalid-material", "material", name));
            return null;
        }
    }

    private Double parseDouble(CommandSender sender, String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.message("invalid-number", "valor", value));
            return null;
        }
    }

    private Integer parseInt(CommandSender sender, String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.message("invalid-number", "valor", value));
            return null;
        }
    }
}
