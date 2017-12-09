package de.melays.bw1o1;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ShopEvent extends Event{
	
    private static final HandlerList handlers = new HandlerList();
	
    Player p;
    
    ItemStack bought;
    int amount;
	
    public ShopEvent(Player shopper , ItemStack bought , int amount) {
    	this.p = shopper;
    	this.bought = bought;
    	this.amount = amount;
    }

    public Player getPlayer() {
        return p;
    }
    
    public ItemStack getItem(){
    	return bought;
    }
    
    public int getAmount(){
    	return amount;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
