package com.panav.wafflereports.gui;
import com.panav.wafflereports.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ReportDeletion implements Listener {
    private final @NotNull MySQL database = MySQL.database;


    //report deletion code
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (!e.getInventory().getName().equalsIgnoreCase(UI.getGui().getName())) {Bukkit.broadcastMessage("gui not being the same"); return;}
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) { Bukkit.broadcastMessage("Returned due to null item"); return;}
        if(!e.isShiftClick()) return;

        Material item = e.getCurrentItem().getType();

        //gathering id from Item lore
        if (item != Material.SKULL_ITEM) return;
        ItemStack itemStack = e.getCurrentItem();
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

        List<String> lore = meta.getLore();

        String idString = lore.get(3);
        int id = Integer.parseInt(idString.split(": ")[1]);

        //Deletion from database
        PreparedStatement ps;
        try {
            ps = database.getConnection().prepareStatement("DELETE FROM WreportDB WHERE ID = ?;");
            ps.setInt(1, id);
            ps.executeUpdate();

            player.getOpenInventory().getTopInventory().remove(itemStack);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
