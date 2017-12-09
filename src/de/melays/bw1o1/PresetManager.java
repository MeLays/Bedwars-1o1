package de.melays.bw1o1;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PresetManager {
	
	main plugin;
	
	public PresetManager(main m){
		plugin = m;
	}
	
	public Material getPresetItem (String name){
		ArrayList<String> s = (ArrayList<String>) plugin.getConfig().getStringList("Presets");
		if (s.contains(name)){
			String data = plugin.getConfig().getString("Preset."+name+".item");
			if (data == null){
				return Material.BARRIER;
			}
			else{
				return Material.getMaterial(data);
			}
		}
		else{
			return null;
		}
	}
	
	public Preset getPreset(String name){
		ArrayList<String> s = (ArrayList<String>) plugin.getConfig().getStringList("Presets");
		if (s.contains(name)){
			Location loc1 = plugin.getLocation("Preset."+name+".pos1");
			Location loc2 = plugin.getLocation("Preset."+name+".pos2");
			if (loc1 != null && loc2 != null){
				Preset pr = new Preset (plugin , loc1 , loc2);
				return pr;
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}
	}
	
}
