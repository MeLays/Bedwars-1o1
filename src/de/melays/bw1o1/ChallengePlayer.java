package de.melays.bw1o1;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.md_5.bungee.api.ChatColor;

public class ChallengePlayer {
	
	main plugin;
	Player p;
	
	ArrayList<Challenge> challenges = new ArrayList<Challenge>();
	
	public ChallengePlayer (main m , Player p){
		plugin = m;
		this.p = p;
	}
	
	public void challenge (Player sender , String preset , boolean elo){
		for (Challenge c : new ArrayList<Challenge>(challenges)){
			if (c.requester == sender){
				challenges.remove(c);
			}
		}
		
		challenges.add(new Challenge (plugin , p , sender , preset , elo));
		
	}
	
	
	public boolean accept (Player pl){
		ChallengePlayer cp = plugin.cp.get(pl);
		for (Challenge c : cp.challenges){
			if (c.requester == p){
				c.accept();
				plugin.cp.put(pl, new ChallengePlayer(plugin , pl));
				plugin.cp.put(p, new ChallengePlayer(plugin , p));
				return true;
			}
		}
		return false;
	}
	
	
	public void callHit(Player hitter){
		
		if (plugin.cp.containsKey(hitter)){
			
			boolean started = false;
			ChallengePlayer cp = plugin.cp.get(hitter);
			for (Challenge c : cp.challenges){
				if (c.requester == p){
					started = true;
					c.accept();
					plugin.cp.put(hitter, new ChallengePlayer(plugin , hitter));
					plugin.cp.put(p, new ChallengePlayer(plugin , p));
				}
			}
			
			if (!started){
				if (!plugin.arenas.containsKey(hitter)){
					plugin.arenas.put(hitter, plugin.presets.get(plugin.randInt(0,plugin.presets.size()-1)));
				}
				String map = plugin.arenas.get(hitter);
				boolean elo = false;
				String elostr = plugin.mf.getMessage("str_no", true);
				if (plugin.elo.containsKey(hitter)){
					elo = plugin.elo.get(hitter);
					if (elo){
						elostr = plugin.mf.getMessage("str_yes", true);
					}
					else{
						elostr = plugin.mf.getMessage("str_no", true);
					}
				}
				challenge (hitter , map , elo);
				String request = plugin.mf.getMessage("send_request", true).replace("%player%", p.getName()).replace("%preset%", map).replace("%stats%", elostr);
				String receive = plugin.mf.getMessage("receive_request", true).replace("%player%", hitter.getName()).replace("%preset%", map).replace("%stats%", elostr);
				hitter.sendMessage(request);
				p.sendMessage(receive);
				hitter.playSound(hitter.getLocation(), Sound.LEVEL_UP, 1, 1);
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			}
			
		}
		else{
			plugin.cp.put(hitter , new ChallengePlayer (plugin , hitter));
			callHit(hitter);
		}
		
	}
	
	
}
