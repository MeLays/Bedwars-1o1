package de.melays.bw1o1;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ArenaManager {
	
	public main plugin;
	public HashMap<Integer , Arena> arenas;
	public int arenaid = 0;
	
	public ArenaManager(main m){
		arenas = new HashMap<Integer , Arena>();
		plugin = m;
	}
	
	public ArrayList<Player> getAllPlayers(){
		
		ArrayList<Player> ps = new ArrayList<Player>();
		
		for (Integer ai : arenas.keySet()){
			Arena a = arenas.get(ai);
			try{
				ps.add(a.p1);
				ps.add(a.p2);
			}
			catch(Exception ex){
				
			}
		}
		
		return ps;
	}
	
	public ArrayList<Player> getAllSpecs(){
		
		ArrayList<Player> ps = new ArrayList<Player>();
		
		for (Integer ai : arenas.keySet()){
			Arena a = arenas.get(ai);
			try{
				ps.addAll(a.specs);
			}
			catch(Exception ex){
				
			}
		}
		
		return ps;
	}
	
	public Arena searchPlayer(Player p){
		for (Integer ai : arenas.keySet()){
			Arena a = arenas.get(ai);
			if (a.p1 == p || a.p2 == p){
				return a;
			}
			for (Player pl : a.specs){
				if (pl == p){
					return a;
				}
			}
		}
		return null;
	}
	
	public int createArena(Player p1 , Player p2 , Preset preset , boolean elo){
		
		PresetList plist = preset.generateGenerateList();
		
		if (searchPlayer(p1) == null && searchPlayer(p2) == null){
			
			Location spawnloc = new Location (Bukkit.getWorld(plugin.getConfig().getString("gameworld")) , ((arenas.size()+1) * 500 ) * arenaid, 70 , 0 );
			plist.generatePreset(spawnloc , p1 , p2);
			Arena a = new Arena (plugin , p1 , p2 , plist.p1 , plist.p2 , plist , spawnloc , elo);
			plist.setArena(a);
			a.fetchTags();
			arenaid += 1;
			arenas.put(arenaid, a);
			return arenaid;
			
		}
		
		return 0;
	}
	
}
