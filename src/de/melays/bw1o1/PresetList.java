package de.melays.bw1o1;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PresetList {
	
	int x;
	int y;
	int z;
	Location p1;
	Location p2;
	ArrayList<AdvancedMaterial> list;
	boolean move = true;
	main plugin;
	Location pos1;
	int count;
	int countx;
	int scheu = 0;
	Arena a;
	
	int sch1;
	int sch2;
	int sch3;
	
	ArenaMeta meta;
	
	boolean generating;
	
	ArrayList<Location> bronze = new ArrayList<Location>();
	ArrayList<Location> iron = new ArrayList<Location>();
	ArrayList<Location> gold = new ArrayList<Location>();
	HashMap<Location , AdvancedMaterial> instable = new HashMap<Location , AdvancedMaterial>();
	
	public PresetList(main m , ArrayList<AdvancedMaterial> l , int xsize , int ysize , int zsize){
		x = xsize;
		y = ysize;
		z = zsize;
		list = l;
		plugin = m;
	}
	
	public PresetList generatePreset(Location loc , Player pl1 , Player pl2){
		pos1 = loc;
		int spawnx = x / 2;
		int spawny = y / 2;
		int spawnz = z / 10;
		count = 0;
		p1 = new Location (pos1.getWorld() , pos1.getX() +spawnx , pos1.getY() +spawny , pos1.getZ() +spawnz);
		p2 = new Location (pos1.getWorld() , pos1.getX() +x-spawnx-1 , pos1.getY() +spawny , pos1.getZ() +z-spawnz-1);
		p1.getBlock().setType(Material.GLOWSTONE);
		p2.getBlock().setType(Material.GLOWSTONE);
		move = false;
		if (pl1 != null && pl2 != null){
			pl1.sendMessage(plugin.prefix + " Generiere die Welt...");
			pl2.sendMessage(plugin.prefix + " Generiere die Welt...");
		}
		itterateX (x , pl1 , pl2);
		//RekforX(x , 0 , 1 , z , y , loc);
//		for (int i = 0 ; i < x ; i ++){
//
//		}
		p1.getBlock().setType(Material.GLOWSTONE);
		p2.getBlock().setType(Material.GLOWSTONE);
		
		if (pl2 != null){
				
			sch1 = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					
					if (a != null  && !generating){
						if (!a.running){
							Bukkit.getScheduler().cancelTask(sch1);
						}
						for (Location l : bronze){
							
							Item i = l.getWorld().dropItem( a.getMiddle(l) , plugin.getCustomItem(Material.CLAY_BRICK, 1, (byte) 0 , ChatColor.RED + "Bronze" , "", false));
							i.teleport(a.getMiddle(l));
							i.setVelocity(new Vector (0 ,0 , 0));
							
						}
					}
					
				}
					
			}, 0L , plugin.settings.get(pl2.getUniqueId()).bronze);
			
			sch2 = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					
					if (a != null  && !generating){
						if (!a.running){
							Bukkit.getScheduler().cancelTask(sch2);
						}
						for (Location l : gold){
							
							Item i = l.getWorld().dropItem( a.getMiddle(l) , plugin.getCustomItem(Material.GOLD_INGOT, 1, (byte) 0 , ChatColor.GOLD + "Gold" , "", false));
							i.teleport(a.getMiddle(l));
							i.setVelocity(new Vector (0 ,0 , 0));
							
						}
					}
					
				}
					
			}, 0L ,  plugin.settings.get(pl2.getUniqueId()).gold*20);
			
			sch3 = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					
					if (a != null && !generating){
						if (!a.running){
							Bukkit.getScheduler().cancelTask(sch3);
						}
						for (Location l : iron){
							
							Item i = l.getWorld().dropItem( a.getMiddle(l) , plugin.getCustomItem(Material.IRON_INGOT, 1, (byte) 0 , ChatColor.GRAY + "Eisen" , "", false));
							i.teleport(a.getMiddle(l));
							i.setVelocity(new Vector (0 ,0 , 0));
							
						}
					}
					
				}
					
			}, 200L ,  plugin.settings.get(pl2.getUniqueId()).iron*20);
			
			return this;
		}
		return this;
	}
	
	public void setArena(Arena ar){
		a = ar;
	}
	
	public void cancelGenerator(Player p1 , Player p2){
		if (generating){
			Bukkit.getScheduler().cancelTask(scheu);
			generating = false;
			plugin.dontmove.remove(p1);
			plugin.dontmove.remove(p2);
		}
	}
	
	
	public void itterateX (final int amount , final Player p1 , final Player p2 ){
		plugin.dontmove.add(p1);
		plugin.dontmove.add(p2);
		instable = new HashMap<Location , AdvancedMaterial>();
		int speed = plugin.getConfig().getInt("generator");
		if (p1 != null && p2 != null){
			if (p1.hasPermission("bw.speedgen") || p2.hasPermission("bw.speedgen")){
				speed = plugin.getConfig().getInt("speedgenerator");
				p1.sendMessage(plugin.mf.getMessage("speedgenerator", true));
				p2.sendMessage(plugin.mf.getMessage("speedgenerator", true));
			}
		}
		for (Entity e : pos1.getWorld().getNearbyEntities(pos1, 50, 50, 50)){
			if (e instanceof Item){
				e.remove();
			}
		}
		meta = new ArenaMeta ();
		System.out.println("[BW1o1] Scanning for Meta-Signs ...");
		generating = true;
		scheu = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				int i = countx;
				for (int j = 0 ; j < y ; j ++){
					for (int m = 0 ; m < z ; m ++){
						if (list.get(count).m != null){
							Location locblock = new Location (pos1.getWorld() , pos1.getX() + i , pos1.getY() + j , pos1.getZ() + m);
							BlockState state = list.get(count).state;
							if (!list.get(count).m.isSolid() || list.get(count).m == Material.BED_BLOCK){
								instable.put(locblock ,  list.get(count));
							}
							else if(state instanceof Sign){
								
							}
							else{
								locblock.getBlock().setType(list.get(count).m);
								locblock.getBlock().setData(list.get(count).b);
							}
							if (locblock.getBlock().getType() == Material.HARD_CLAY){
								bronze.add(locblock.getBlock().getRelative(BlockFace.UP , 1).getLocation());
							}
							else if (locblock.getBlock().getType() == Material.IRON_BLOCK){
								iron.add(locblock.getBlock().getRelative(BlockFace.UP , 1).getLocation());
							}
							else if (locblock.getBlock().getType() == Material.GOLD_BLOCK){
								gold.add(locblock.getBlock().getRelative(BlockFace.UP , 1).getLocation());
							}
							if (state != null){
								if (state instanceof Sign){
									Sign sign = (Sign) state;
									if (sign.getLine(0).equals("TAG")){
										if (!sign.getLine(1).equals("")){
											String s = sign.getLine(1);
											if (s.equalsIgnoreCase("VILLAGER") || s.equalsIgnoreCase("SPAWN")){
												meta.addData(sign.getLine(1).toUpperCase(), locblock);
											}
											else{
												meta.addData(sign.getLine(1), locblock);
											}
										}
									}
								}
							}
						}
						count += 1;
					}
				}
				if (p1 != null && p2 != null){
					p1.playSound(p1.getLocation(), Sound.CLICK, 1, 1);
					p2.playSound(p2.getLocation(), Sound.CLICK, 1, 1);
				}
				countx += 1;
				if (countx == amount){
					if (p1 != null && p2 != null){
						System.out.println("[BW1o1] Found "+meta.meta.size()+ " Tags on Signs.");
						p1.sendMessage(plugin.prefix + " Die Welt wurde erfolgreich generiert!");
						p2.sendMessage(plugin.prefix + " Die Welt wurde erfolgreich generiert!");
						p1.sendTitle(plugin.mf.getMessage("title_start", true), plugin.mf.getMessage("title_start_under", true));
						p2.sendTitle(plugin.mf.getMessage("title_start", true), plugin.mf.getMessage("title_start_under", true));
						for (Location l : instable.keySet()){
							Material m = instable.get(l).m;
							byte d = instable.get(l).b;
							l.getBlock().setType(m);
							l.getBlock().setData(d);
						}
						plugin.dontmove.remove(p1);
						plugin.dontmove.remove(p2);
						try {
							a.fetchTags();
						}
						catch (Exception ex){
							
						}
					}
					generating = false;
					Bukkit.getScheduler().cancelTask(scheu);
					plugin.settings.get(p2.getUniqueId()).sendMessage(p1);
					plugin.settings.get(p2.getUniqueId()).sendMessage(p2);
					TextComponent message = new TextComponent(plugin.prefix + ChatColor.RED + " Hier klicken zum Ablehnen!");
					message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/cancle" ) );
					message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Lehne die Einstellugen des Spielers ab!").create() ) );
					p1.spigot().sendMessage( message );
					p2.sendMessage("");
				}
			}
			
		}, 20L , speed);
	}
	
//	public void RekforX (final int till , final int temp , final int time , final int z , final int y , final Location loc){
//		if (temp != till){
//			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//				@Override
//				public void run() {
//					Bukkit.broadcastMessage("Generating Cuboid (X) at "+ temp);
//					RekforY (y , 0 , time , z , temp , loc );
//					RekforX(till , temp + 1 , time , z , y , loc );
//				}
//				
//			}, time);
//		}
//	}
//	
//	public void RekforY (final int till , final int temp , final int time , final int z , final int x , final Location loc){
//		if (temp != till){
//			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//				@Override
//				public void run() {
//					Bukkit.broadcastMessage("Generating Row (Y) at "+ temp);
//					RekforZ (z , 0 , time , temp , x , loc );
//					RekforY(till , temp + 1 , time , z , x , loc );
//				}
//				
//			}, time);
//		}
//	}
//	
//	public void RekforZ (final int till , final int temp , final int time , final int y , final int x , final Location loc){
//		if (temp != till){
//			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//				@Override
//				public void run() {
//					Bukkit.broadcastMessage("Generating Block (Z) at "+ temp);
//					Location locblock = new Location (loc.getWorld() , loc.getX() + x , loc.getY() + y , loc.getZ() + z);
//					locblock.getBlock().setType(list.get(count).m);
//					locblock.getBlock().setData(list.get(count).b);
//					count = count + 1;
//					RekforZ(till , temp + 1 , time , z , x , loc);
//				}
//				
//			}, time);
//		}
//		else{
//			move = true;
//		}
//	}
	
	public void remove (Location l){
		Location pos1 = l;
		for (int i = 0 ; i < x ; i ++){
			for (int j = 0 ; j < y ; j ++){
				for (int m = 0 ; m < z ; m ++){
					Location locblock = new Location (pos1.getWorld() , pos1.getX() + i , pos1.getY() + j , pos1.getZ() + m);
					locblock.getBlock().setType(Material.AIR);
				}
			}
		}
	}
	
}
