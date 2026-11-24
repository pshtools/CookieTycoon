package com.tools.cookieTycoon.commands;

import com.tools.cookieTycoon.data.PlayerDataManager;
import com.tools.cookieTycoon.game.CookieManager;
import com.tools.cookieTycoon.game.UpgradesManager;
import com.tools.cookieTycoon.model.UpgradeType;
import com.tools.cookieTycoon.utils.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class AdminCommands implements CommandExecutor, TabCompleter {

    private static final List<String> ACTIONS = List.of("add", "set", "remove", "wipe");

    private final PlayerDataManager players;
    private final CookieManager cookies;
    private final UpgradesManager upgrades;

    public AdminCommands(PlayerDataManager players, CookieManager cookies, UpgradesManager upgrades) {
        this.players = players;
        this.cookies = cookies;
        this.upgrades = upgrades;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean upgradeCommand = command.getName().equalsIgnoreCase("upgrade");
        String usage = upgradeCommand
                ? "§cИспользуйте: /upgrade <add|set|remove|wipe> <ник> [апгрейд] [кол-во]"
                : "§cИспользуйте: /ck <add|set|remove|wipe> <ник> [кол-во]";

        if (args.length < 2 || !ACTIONS.contains(args[0].toLowerCase())) {
            sender.sendMessage(usage);
            return true;
        }
        String action = args[0].toLowerCase();

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null || players.get(target.getUniqueId()) == null) {
            sender.sendMessage("§cИгрок не найден!");
            return true;
        }

        if (upgradeCommand) {
            handleUpgrade(sender, target, action, args);
        } else {
            handleCookies(sender, target, action, args);
        }
        return true;
    }



    private void handleCookies(CommandSender sender, Player target, String action, String[] args) {
        if (action.equals("wipe")) {
            cookies.wipe(target);
            sender.sendMessage("§aПеченьки игрока §6" + target.getName() + "§a обнулены");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage("§cВведите количество печенек!");
            return;
        }
        double amount = parseAmount(args[2]);
        if (Double.isNaN(amount)) {
            sender.sendMessage("§cНекорректное количество печенек!");
            return;
        }
        String formatted = NumberFormatter.format(amount);

        switch (action) {
            case "add":
                if (amount <= 0) {
                    sender.sendMessage("§cКоличество должно быть положительным!");
                    return;
                }
                cookies.add(target, amount);
                sender.sendMessage("§aДобавлено §6" + formatted + "§a печенек игроку §6" + target.getName());
                break;
            case "remove":
                if (amount <= 0) {
                    sender.sendMessage("§cКоличество должно быть положительным!");
                    return;
                }
                cookies.remove(target, amount);
                sender.sendMessage("§aУдалено §6" + formatted + " §aпеченек у игрока §6" + target.getName());
                break;
            default:
                cookies.set(target, amount);
                sender.sendMessage("§aУстановлено §6" + formatted + " §aпеченек игроку §6" + target.getName());
                break;
        }
    }



    private void handleUpgrade(CommandSender sender, Player target, String action, String[] args) {
        if (action.equals("wipe")) {
            upgrades.wipe(target);
            sender.sendMessage("§aУлучшения игрока §6" + target.getName() + "§a обнулены");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage("§cУкажите апгрейд. Существующие: §e" + String.join("§7, §e", UpgradeType.keys()));
            return;
        }
        UpgradeType type = UpgradeType.byKey(args[2]);
        if (type == null) {
            sender.sendMessage("§cНеизвестный апгрейд. Существующие: §e" + String.join("§7, §e", UpgradeType.keys()));
            return;
        }
        if (args.length < 4) {
            sender.sendMessage("§cВведите количество!");
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cНекорректное количество!");
            return;
        }
        if (amount < 0 || (amount == 0 && !action.equals("set"))) {
            sender.sendMessage("§cКоличество должно быть положительным!");
            return;
        }

        switch (action) {
            case "add":
                upgrades.add(target, type, amount);
                sender.sendMessage("§aДобавлено §6" + amount + " " + type.key() + "§a игроку §6" + target.getName());
                break;
            case "remove":
                upgrades.add(target, type, -amount);
                sender.sendMessage("§aУдалено §6" + amount + " §a" + type.key() + " у игрока §6" + target.getName());
                break;
            default:
                upgrades.set(target, type, amount);
                sender.sendMessage("§aУстановлено §6" + amount + " §a" + type.key() + " игроку §6" + target.getName());
                break;
        }
    }

    
    private static double parseAmount(String text) {
        try {
            double value = Double.parseDouble(text);
            return Double.isFinite(value) && value >= 0 ? value : Double.NaN;
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return partial(args[0], ACTIONS);
        }
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3 && command.getName().equalsIgnoreCase("upgrade") && !args[0].equalsIgnoreCase("wipe")) {
            return partial(args[2], UpgradeType.keys());
        }
        return Collections.emptyList();
    }

    private static List<String> partial(String token, List<String> options) {
        List<String> result = new ArrayList<>();
        StringUtil.copyPartialMatches(token, options, result);
        return result;
    }
}
