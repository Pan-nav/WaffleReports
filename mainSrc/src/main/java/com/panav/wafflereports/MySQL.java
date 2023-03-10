package com.panav.wafflereports;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private final WaffleReports wreports = WaffleReports.instance;

    public static MySQL database;
    public Connection connection;

    public void connect() throws SQLException {

        String HOST = wreports.getConfig().getString("MYSQL.HOST");
        int PORT = wreports.getConfig().getInt("MYSQL.PORT");
        String USERNAME = wreports.getConfig().getString("MYSQL.USERNAME");
        String DATABASE = wreports.getConfig().getString("MYSQL.DATABASE");
        String PASSWORD = wreports.getConfig().getString("MYSQL.PASSWORD");
        database = this;
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false&autoReconnect=true",
                USERNAME,
                PASSWORD);

        createTable();
    }

    public boolean isConnected(){ return connection != null; }

    public final @NotNull Connection getConnection() { return connection; }

    public void disconnect(){
        if (isConnected()){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable(){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS WreportDB (ID int AUTO_INCREMENT, Accused varchar(16), ReportedBy varchar(16), DATE varchar(16),Reason varchar(255), PRIMARY KEY(ID));");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public @NotNull List<ItemStack> getAccused() throws SQLException {
        PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM WreportDB;");
        ResultSet rs = statement.executeQuery();

        List<ItemStack> itemStacks = new ArrayList<>();

        while (rs.next()){

            List<String> lore = new ArrayList<>();

            String accused = rs.getString("Accused");
            String reporter = rs.getString("ReportedBy");
            String date = rs.getString("DATE");
            String reason = rs.getString("Reason");
            int id = rs.getInt("ID");

            lore.add(ChatColor.AQUA + "Reported By: " + reporter);
            lore.add(ChatColor.AQUA + "Date Reported: " + date);
            lore.add(ChatColor.AQUA + "Reason: " + reason);
            lore.add(ChatColor.AQUA + "ID: " + id);

            final ItemStack heads = new ItemStack(Material.SKULL_ITEM, 1, (short) 3 );
            final SkullMeta meta = (SkullMeta) heads.getItemMeta();
            meta.setOwner(accused);
            meta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + accused);
            meta.setLore(lore);
            heads.setItemMeta(meta);
            itemStacks.add(heads);


        } return itemStacks;

    }

}
