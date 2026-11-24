package com.tools.cookieTycoon.commands;

import com.tools.cookieTycoon.ui.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class PlayerCommands implements CommandExecutor {

    private final Menus menus;

    public PlayerCommands(Menus menus) {
        this.menus = menus;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам.");
            return true;
        }
        Player player = (Player) sender;

        switch (command.getName().toLowerCase()) {
            case "menu":
                menus.openUpgrades(player);
                return true;
            case "rebirth":
                menus.openRebirth(player);
                return true;
            case "ach":
                menus.openAchievements(player);
                return true;
            case "info":
                if (args.length == 0) {
                    sender.sendMessage("§cИспользуйте: /info <ник>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    sender.sendMessage("§cИгрок не найден!");
                    return true;
                }
                menus.openInfo(player, target);
                return true;
            default:
                return false;
        }
    }
}
