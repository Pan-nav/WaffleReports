package com.panav.wafflereports.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class GUIListener implements Listener {


    @EventHandler
    public void onInvClick(InventoryClickEvent e)  throws SQLException {

        ItemStack limePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemStack redPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("Reports")){
            int page = Integer.parseInt((e.getView().getTitle().split(" - ")[1]));
            if (e.getRawSlot() == 0 && e.getCurrentItem().equals(limePane)){
                new UI((Player)e.getWhoClicked(), page - 1);
            } else if (e.getRawSlot() == 8 && e.getCurrentItem().equals(redPane)){
                new UI((Player)e.getWhoClicked(), page + 1);
            }
        }
        e.setCancelled(true);
    }
}

