package com.panav.wafflereports.gui;

import com.panav.wafflereports.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class UI {
    MySQL database = MySQL.database;
    public static HashMap<Player, UI> uis = new HashMap<>();
    public static Inventory thisGui;



    public UI(Player player, int page) throws SQLException {
        Inventory gui = Bukkit.createInventory(null, 54, "Reports - " + page);

        List< ItemStack> reports = database.getAccused();

        ItemStack limePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemStack redPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

        ItemStack left;
        ItemMeta leftMeta;

        if (PageUtil.isPageValid(reports, page - 1, 52)){
            left = new ItemStack(limePane);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Go Back");
        } else {
            left = new ItemStack(redPane);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD +  "Go Back");
        }

        left.setItemMeta(leftMeta);
        gui.setItem(0,left);

        ItemStack right;
        ItemMeta rightMeta;

        if (PageUtil.isPageValid(reports, page + 1, 52)){
            right = new ItemStack(limePane);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Go Forward");
        } else {
            right = new ItemStack(redPane);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD +  "Go Forward");
        }

        right.setItemMeta(rightMeta);
        gui.setItem(8,right);

        for (ItemStack itemStack : PageUtil.getPageItems(reports,page,52)){
            gui.setItem(gui.firstEmpty(), itemStack);
        }
        thisGui = gui;
        uis.put(player, this);
        player.openInventory(gui);
    }



    public static Inventory getGui() {
        return thisGui;
    }
}
