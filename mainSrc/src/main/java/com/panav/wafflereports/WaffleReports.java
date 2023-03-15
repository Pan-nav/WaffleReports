package com.panav.wafflereports;

import com.panav.wafflereports.Core.CancelReport;
import com.panav.wafflereports.Core.ReportsHandler;
import com.panav.wafflereports.Core.wreports;
import com.panav.wafflereports.GUI.GUIListener;
import com.panav.wafflereports.GUI.ReportDeletion;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class WaffleReports extends JavaPlugin implements Listener {

    public static WaffleReports instance;
    private MySQL database;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        Database();

        getCommand("report").setExecutor(new ReportsHandler());
        getCommand("wreport").setExecutor(new wreports());
        getCommand("closeBook").setExecutor(new CancelReport());

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(),this);
        Bukkit.getPluginManager().registerEvents(new ReportDeletion(), this);
    }

    @Override
    public void onDisable() {
        database.disconnect();
        System.out.println(getPrefix() + " Database Disconnected!");
        System.out.println(getPrefix() + " Thank you for Using Waffle Reports!");

    }

    public void Database(){
        //database
        database = new MySQL();
        try {
            database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(getPrefix() + " Error connecting with MySQL!");
        }
        System.out.println(getPrefix() +  " Waffle Reports database connected = " + database.isConnected());
    }

    @NotNull
    public String getPrefix() { return  "[" + getConfig().getString("prefix") + "]";}

    @EventHandler
    public void onReport(){

    }
}
