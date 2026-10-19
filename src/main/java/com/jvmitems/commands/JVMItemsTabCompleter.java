package com.jvmitems.commands;

import com.jvmitems.JVMItems;
import com.jvmitems.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JVMItemsTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "help", "list", "info", "create", "delete", "give", "setname", "addlore", "clearlore",
            "setmaterial", "setdamage", "setarmor", "setarmortoughness", "setunbreakable", "enchant",
            "power", "reload"
    );

    private static final List<String> POWER_ACTIONS = Arrays.asList("add", "remove", "list", "setfireball", "condition");
    private static final List<String> POWER_TYPES = Arrays.asList(
            "arrow", "fireball", "snowball", "ice", "potion", "lifesteal", "damagemultiplier",
            "knockback", "command", "lightning", "heal", "firework", "aoedamage"
    );
    private static final List<String> TRIGGERS = Arrays.asList(
            "right_click", "left_click", "sneak_right_click", "sneak_left_click",
            "hit", "take_damage", "block_break", "projectile_hit"
    );
    private static final List<String> CONDITION_ACTIONS = Arrays.asList("add", "remove");
    private static final List<String> CONDITION_TYPES = Arrays.asList(
            "permission", "sneaking", "notsneaking", "healthbelow", "healthabove", "chance"
    );
    private static final List<String> POTION_TARGETS = Arrays.asList("self", "opponent");

    private final JVMItems plugin;

    public JVMItemsTabCompleter(JVMItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "help":
                if (args.length == 2) {
                    return filter(HelpMenu.categories(), args[1]);
                }
                break;
            case "info":
            case "delete":
            case "give":
            case "setname":
            case "addlore":
            case "clearlore":
            case "setmaterial":
            case "setdamage":
            case "setarmor":
            case "setarmortoughness":
            case "setunbreakable":
                if (args.length == 2) {
                    return filter(itemIds(), args[1]);
                }
                if (sub.equals("give") && args.length == 3) {
                    return filter(playerNames(), args[2]);
                }
                if (sub.equals("setmaterial") && args.length == 3) {
                    return filter(materialNames(), args[2]);
                }
                break;
            case "create":
                if (args.length == 3) {
                    return filter(materialNames(), args[2]);
                }
                break;
            case "enchant":
                if (args.length == 2) {
                    return filter(Arrays.asList("add", "remove"), args[1]);
                }
                if (args.length == 3) {
                    return filter(itemIds(), args[2]);
                }
                if (args.length == 4) {
                    return filter(enchantNames(), args[3]);
                }
                break;
            case "power":
                return completePower(args);
            default:
                break;
        }

        return new ArrayList<>();
    }

    private List<String> completePower(String[] args) {
        // args[0] = "power"
        if (args.length == 2) {
            return filter(POWER_ACTIONS, args[1]);
        }
        String action = args[1].toLowerCase();

        if (action.equals("add")) {
            if (args.length == 3) {
                return filter(POWER_TYPES, args[2]);
            }
            if (args.length == 4) {
                return filter(itemIds(), args[3]);
            }
            if (args.length == 5) {
                return filter(TRIGGERS, args[4]);
            }
            if (args.length == 7 && args[2].equalsIgnoreCase("potion")) {
                return filter(POTION_TARGETS, args[6]);
            }
            if (args.length == 8 && args[2].equalsIgnoreCase("potion")) {
                return filter(potionNames(), args[7]);
            }
            return new ArrayList<>();
        }

        if (action.equals("remove") || action.equals("list")) {
            if (args.length == 3) {
                return filter(itemIds(), args[2]);
            }
            if (args.length == 4 && action.equals("remove")) {
                return filter(powerIndexes(args[2]), args[3]);
            }
            return new ArrayList<>();
        }

        if (action.equals("setfireball")) {
            if (args.length == 3) {
                return filter(itemIds(), args[2]);
            }
            if (args.length == 4) {
                return filter(powerIndexes(args[2]), args[3]);
            }
            if (args.length == 5 || args.length == 6) {
                return filter(Arrays.asList("true", "false"), args[args.length - 1]);
            }
            return new ArrayList<>();
        }

        if (action.equals("condition")) {
            if (args.length == 3) {
                return filter(CONDITION_ACTIONS, args[2]);
            }
            if (args.length == 4) {
                return filter(itemIds(), args[3]);
            }
            if (args.length == 5) {
                return filter(powerIndexes(args[3]), args[4]);
            }
            if (args[2].equalsIgnoreCase("add") && args.length == 6) {
                return filter(CONDITION_TYPES, args[5]);
            }
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    private List<String> powerIndexes(String itemId) {
        CustomItem item = plugin.getItemManager().get(itemId);
        if (item == null) {
            return new ArrayList<>();
        }
        List<String> indexes = new ArrayList<>();
        for (int i = 0; i < item.getPowers().size(); i++) {
            indexes.add(String.valueOf(i));
        }
        return indexes;
    }

    private List<String> filter(List<String> options, String partial) {
        String lower = partial.toLowerCase();
        return options.stream().filter(option -> option.toLowerCase().startsWith(lower)).collect(Collectors.toList());
    }

    private List<String> itemIds() {
        return new ArrayList<>(plugin.getItemManager().getItems().keySet());
    }

    private List<String> playerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private List<String> materialNames() {
        return Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList());
    }

    private List<String> enchantNames() {
        return Arrays.stream(Enchantment.values()).map(e -> e.getName().toUpperCase()).collect(Collectors.toList());
    }

    private List<String> potionNames() {
        return Arrays.stream(PotionEffectType.values()).filter(p -> p != null).map(PotionEffectType::getName).collect(Collectors.toList());
    }
}
