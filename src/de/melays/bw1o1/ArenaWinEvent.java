package de.melays.bw1o1;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaWinEvent extends Event{
	
    private static final HandlerList handlers = new HandlerList();
	
	Arena a;
	Player winner;
	boolean draw;
	
    public ArenaWinEvent(Arena a , Player winner) {
    	this.a = a;
    	this.winner = winner;
    }

    public Arena getArena() {
        return a;
    }

    public Player getWinner() {
        return winner;
    }
    
    public boolean isDraw(){
    	return (winner == null);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
