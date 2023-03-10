package com.panav.wafflereports.Core;

import com.panav.wafflereports.MySQL;
import com.panav.wafflereports.WaffleReports;
import com.panav.wreportAPI.PlayerReportEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReportsHandler implements CommandExecutor {
    private final WaffleReports wreports = WaffleReports.instance;

    public HashMap<String, Long> cooldowns = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        //length 0 = no target
        //length 1 = open book
        //length 2+ = send confirmation
        //args ending confirm = submit

        //Use /report <playerName> <Reason> confirm

        //no target
        if (args.length < 1) {
            player.sendMessage(ChatColor.YELLOW + wreports.getPrefix() +  " Incorrect Syntax! Use /report <playerName>");
            return true;
        }

        //accused
        Player target = Bukkit.getPlayer(args[0]);

        //open book gui
        if ((args.length < 2) && (!args[args.length-1].equalsIgnoreCase("confirm"))) {
            onTargetCommand(player, target);
            return true;
        }

        StringBuilder finalReason = new StringBuilder();
        int length = args[args.length-1].equalsIgnoreCase("confirm")?1:0;
        for (int i = 1; i < args.length-length; i++) {
            finalReason.append(args[i]).append(" ");
        }


        //confirm report
        if (args[args.length - 1].equalsIgnoreCase("confirm")) {
            onConfirm(player, target, finalReason.toString());
            return true;
        }
        onReasonClick(player, target, finalReason.toString());

        return false;
    }

    //open book gui
    public void onTargetCommand(Player player, Player target) {
        Audience playerAudience = BukkitAudiences.create(wreports).player(player);

        if (!player.hasPermission("wreport.command")) {
            player.sendMessage(ChatColor.RED + wreports.getPrefix() + " You don't have the permission wreport.command!");
            return;
        }

        if (target == null) {
            player.sendMessage(ChatColor.RED + wreports.getPrefix() + wreports.getConfig().getString("missingTarget"));
            return;
        }


        //Cooldown
        int c = wreports.getConfig().getInt("Cooldown");
        if (this.cooldowns.containsKey(player.getName())) {
            long secondsLeft = (this.cooldowns.get(player.getName()) / 1000L) + (long) c - (System.currentTimeMillis() / 1000L);
            if (secondsLeft > 0L) {
                player.sendMessage(ChatColor.RED + wreports.getPrefix() + " You must wait another " + secondsLeft + " before being able to report again!");
                return;
            }
        }


        //self report prevention
        if (target.getName().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + wreports.getPrefix() + wreports.getConfig().getString("SelfPrompt"));
            return;
        }

        Component reasons = Component.text("Report " + target.getName() + ": " + "\n");


        //Main shit
        for (String s : wreports.getConfig().getStringList("Reasons") ) {
            Component reason = Component.text("\n◎ " + s)
                    .hoverEvent(HoverEvent.showText(Component.text("Report " + target.getName() + " for " + s)))
                    .clickEvent(ClickEvent.runCommand("/report " + target.getName() + " " + s));
            reasons = reasons.append(reason);
        }

        Component bookTitle = Component.text(ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "Reporting " + target.getName());
        Component bookAuthor = Component.text(player.getName());
        Book book = Book.book(bookTitle, bookAuthor, reasons);
        playerAudience.openBook(book);
    }


    //Open book for reporting reasons
    public void onReasonClick(Player player, Player target, String reason) {
        Component confirmation = Component.text("");

        Audience playerAudience = BukkitAudiences.create(wreports).player(player);

        Component confirmReason = Component.text("You're about to " + "\n" + "report " + target.getName() + " for " + reason + "\n\n\n\n\n");
        Component Submit = Component.text(ChatColor.GREEN.toString() + ChatColor.BOLD + "Submit Report" + "\n").clickEvent(ClickEvent.runCommand("/report " + target.getName() + " " + reason + " confirm"));
        Component Cancel = Component.text(ChatColor.RED.toString() + ChatColor.BOLD + "Cancel Report").clickEvent(ClickEvent.runCommand("/closeBook" ));

        confirmation = confirmation.append(confirmReason);
        confirmation = confirmation.append(Submit);
        confirmation = confirmation.append(Cancel);

        Component bookTitle = Component.text(ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "Reporting " + target.getName());
        Component bookAuthor = Component.text(player.getName());

        Book book = Book.book(bookTitle, bookAuthor, confirmation);

        playerAudience.openBook(book);

    }


    //What happens when the player clicks confirm
    public void onConfirm(Player player, Player target, String finalReason) {
        player.sendMessage(ChatColor.YELLOW + "Thank you for reporting " + target.getName() + " for " + finalReason);
        cooldowns.put(player.getName(), System.currentTimeMillis() + wreports.getConfig().getLong("Cooldown") * 1000);

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("wreport.staff")) {

                TextComponent gotoText = Component.text(ChatColor.BLUE.toString() + ChatColor.BOLD + "Teleport").hoverEvent(HoverEvent.showText(Component.text("Click me to teleport to " + target.getName()))).clickEvent(ClickEvent.runCommand("/wreport goto " + target.getName()));

                staff.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "--New Report Received--");
                staff.sendMessage(ChatColor.BLUE + " ");
                staff.sendMessage(ChatColor.BLUE + "Reported by: " + player.getName());
                staff.sendMessage(ChatColor.BLUE + "  ");
                staff.sendMessage(ChatColor.BLUE + "Accused Player: " + target.getName());
                staff.sendMessage(ChatColor.BLUE + "   ");
                staff.sendMessage(ChatColor.BLUE + "Reason: " + finalReason);
                staff.sendMessage(ChatColor.BLUE + "   ");

                Audience gotoPlayer = BukkitAudiences.create(wreports).player(staff);
                gotoPlayer.sendMessage(gotoText);
                staff.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "----------------------");
            }

            setReport(target.getName(), player.getName(), finalReason);

            return;
        }
    }

    //inputting data in Mysql
    public void setReport(final String target, final String player, final String finalReason) {
        Date idk = new Date();
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(date.format(idk));
        Bukkit.broadcast(date.format(idk), "wreport.report");

        PreparedStatement mysql;
        try {
            mysql = MySQL.database.getConnection().prepareStatement("INSERT INTO WreportDB (Accused,ReportedBy,Date,Reason) VALUES (?,?,?,?);");
            mysql.setString(1, target);
            mysql.setString(2, player);
            mysql.setString(3, date.format(idk));
            mysql.setString(4, finalReason);
            mysql.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            mysql = MySQL.database.getConnection().prepareStatement("SELECT MAX(ID) FROM WreportDB;");
            ResultSet rs = mysql.executeQuery();
            rs.next();
            int id = rs.getInt(1);

           PlayerReportEvent reportEvent = new PlayerReportEvent(Bukkit.getPlayer(player), finalReason, Bukkit.getPlayer(target), id);
           Bukkit.getPluginManager().callEvent(reportEvent);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            mysql.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}




