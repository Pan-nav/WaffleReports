package com.panav.wafflereports.Core;

import com.panav.wafflereports.GUI.UI;
import com.panav.wafflereports.WaffleReports;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class wreports implements CommandExecutor{
    private final WaffleReports wreports = WaffleReports.instance;

    protected Player player;
    protected Player target;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return true;
        this.player = (Player) sender;

        if (!player.hasPermission("wreport.staff")) return true;

        if (!(args.length > 0)) {
            player.sendMessage(ChatColor.YELLOW + "Invalid usage!" + "\n"
                    + "/wreport goto <playername>" + "\n"
                    + "/wreport reload" + "\n"
                    + "/wreport reports"
            );
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "goto":

                if (Bukkit.getPlayer(args[1]) != null) {
                    this.target = Bukkit.getPlayer(args[1]);
                } else {
                    player.sendMessage(ChatColor.RED + wreports.getConfig().getString("missingTarget"));
                }

                if (!wreports.getConfig().getBoolean("Go-To")) {
                    player.sendMessage(ChatColor.YELLOW + wreports.getPrefix() + " Goto is disabled in the config!");
                    return true;
                }

                World world = target.getWorld();
                Location loc = target.getLocation();
                Location targetLoc = new Location(world, loc.getX(), loc.getY(), loc.getZ());

                if (player.getWorld() == world){
                    player.teleport(loc);
                } else {
                    player.teleport(targetLoc);
                }

                player.sendMessage(ChatColor.YELLOW + wreports.getPrefix() + " You have been teleported to the accused.");

                break;

            case "reload":
                wreports.reloadConfig();
                player.sendMessage(ChatColor.YELLOW + wreports.getPrefix() + " Config Reloaded!");
                break;

            case "reports":
                try {
                    new UI(player, 1);
                } catch (SQLException e) {
                    System.out.println("Unable to view reports");
                }
                break;

            case "default":
                player.sendMessage(ChatColor.YELLOW + wreports.getPrefix() + " Command not found!");
        }

        return false;
    }

}
