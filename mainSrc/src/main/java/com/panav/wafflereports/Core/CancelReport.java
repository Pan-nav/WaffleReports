package com.panav.wafflereports.Core;

import com.panav.wafflereports.WaffleReports;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelReport implements CommandExecutor {

    private final WaffleReports wreports = WaffleReports.instance;

    private ReportsHandler reportsHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){ sender.sendMessage(ChatColor.YELLOW + wreports.getPrefix() + " Report Cancelled!"); }



        return false;
    }
}
