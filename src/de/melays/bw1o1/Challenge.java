package de.melays.bw1o1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Challenge {
	
	main plugin;
	Player receiver;
	Player requester;
	String preset;
	boolean e;
	
	public Challenge (main m , Player receiver , Player requester , String preset , boolean elo){
		
		plugin = m;
		this.receiver = receiver;
		this.requester = requester;
		this.preset = preset;
		e = elo;
		
	}
	
	public void accept(){
		Preset p;
		p = plugin.pm.getPreset(preset);
		if (p == null){
			receiver.sendMessage(plugin.prefix + "Die ausgewählte Map konnte nicht gefunden werden!");
			requester.sendMessage(plugin.prefix + "Die ausgewählte Map konnte nicht gefunden werden!");
		}
		else{
			plugin.am.arenaid ++;
			plugin.am.createArena(receiver, requester, p , e);
		}
		
	}
	
}
