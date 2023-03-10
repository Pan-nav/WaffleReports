package com.panav.wreportAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerReportEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;

    private final Player player;
    private final String reason;
    private final Player target;
    private final int id;

    public PlayerReportEvent(Player player, String reason, Player target, int id){
        cancelled = false;

        this.player = player;
        this.reason = reason;
        this.target = target;
        this.id = id;
    }

    public Player getPlayer(){ return player;}
    public String getReason(){return reason;}
    public Player getTarget(){ return target;}

    public int getId(){ return id;}


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }



}
