package de.melays.bw1o1;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaStopEvent extends Event{
	
    private static final HandlerList handlers = new HandlerList();
	
	Arena a;
	Player winner;
	Player looser;
	
    public ArenaStopEvent(Arena a , Player p1 , Player p2) {
    	this.a = a;
    	this.winner = p1;
    	this.looser = p2;
    }

    public Arena getArena() {
        return a;
    }

    public Player getPlayer1() {
        return winner;
    }

    public Player getPlayer2() {
        return looser;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
