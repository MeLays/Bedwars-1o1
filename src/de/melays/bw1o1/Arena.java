package de.melays.bw1o1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class Arena {

	public Player p1;
	public Player p2;
	public Location l1;
	public Location l2;
	public Location start;
	public main plugin;
	public PresetList list;
	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<Player> specs = new ArrayList<Player>();
	
	public boolean bedp1 = true;
	public boolean bedp2 = true;
	public boolean running = true;
	public boolean elo;
	
	public ArrayList<Location> gold = new ArrayList<Location>();
	public ArrayList<Location> iron = new ArrayList<Location>();
	public ArrayList<Location> bronze = new ArrayList<Location>();
	
	public int sch1;
	public int sch2;
	public int sch3;
	
	public Location getMiddle(Location l){
		Location ml = new Location(l.getWorld() , l.getX() +0.5 , l.getY() + 0.5 , l.getZ() +0.5 , l.getYaw() , l.getPitch());
		return ml;
	}
	
	public void addSpec (final Player p){
		if (running){
			p.teleport(p2.getLocation());
			p.showPlayer(p1);
			p.showPlayer(p2);
			p.getInventory().clear();
			p.setGameMode(GameMode.SPECTATOR);
			specs.add(p);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					p.setGameMode(GameMode.SPECTATOR);
				}
			}, 5L);
			updateTabColor();
		}
	}
	
	public ArrayList<Player> getAllPlayers (){
		ArrayList<Player> ps = new ArrayList<Player>();
		ps.add(p1);
		ps.add(p2);
		ps.addAll(specs);
		return ps;
	}
	
	public Color getColor (Player p){
		if (p == p1){
			return plugin.settings.get(p.getUniqueId()).primary;
		}
		else {
			if (plugin.settings.get(p2.getUniqueId()).primary.color == getColor(p1).color){
				return plugin.settings.get(p2.getUniqueId()).sekundary;
			}
			return plugin.settings.get(p2.getUniqueId()).primary;
		}
	}
	
	public void updateTabColor(){
		
		ColorTabAPI.clearTabStyle(p1, getAllPlayers());
		ColorTabAPI.clearTabStyle(p2, getAllPlayers());
		ColorTabAPI.setTabStyle(p1, getColor(p1).toChatColor()+"" , "", 100, getAllPlayers());
		ColorTabAPI.setTabStyle(p2, getColor(p2).toChatColor()+"" , "", 100, getAllPlayers());
		
	}
	
	Location spawn1;
	Location spawn2;
	
	ArrayList<Entity> Villagers = new ArrayList<Entity>();
	
	ArrayList<Object> holograms = new ArrayList<Object>();
	
	public void fetchTags(){
		HashMap<String , Location> meta = new HashMap<String , Location>();
		meta = (HashMap<String, Location>) list.meta.meta.clone();
		
		ArrayList<Location> spawns = list.meta.getTeamData("SPAWN", p1 , this);
		
		try{
			if (spawns.size() >= 1){
				spawn1 = spawns.get(0).getBlock().getRelative(BlockFace.DOWN , 1).getLocation();
			}
		}catch(Exception ex){}
		
		try{
			spawns = list.meta.getTeamData("SPAWN", p2 , this);
			if (spawns.size() >= 1){
				spawn2 = spawns.get(0).getBlock().getRelative(BlockFace.DOWN , 1).getLocation();
			}
		}catch(Exception ex){}
		
		try{
			spawns = list.meta.getMeta("VILLAGER");
			for (Location ll : spawns){
				Entity vill = this.getMiddle(ll).getWorld().spawnEntity(this.getMiddle(ll), EntityType.VILLAGER);
				Villagers.add(vill);
			}
		}catch(Exception ex){}
		
		respawnPlayer(p1 , true);
		respawnPlayer(p2 , true);
		
		if (plugin.useHolographicDisplays){
			for (String s : meta.keySet()){
				
				if (!s.startsWith("SPAWN") && !s.startsWith("VILLAGER")){
					Hologram hologram = HologramsAPI.createHologram(plugin, this.getMiddle(meta.get(s)));
					hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', s.split("_")[0]));
				}
				
			}
		}
	}
	
	
	public Arena(main m , Player pp1 , Player pp2 , Location loc1 , Location loc2 , PresetList l , Location start , boolean elo){
		plugin = m;
		p1 = pp1;
		p2 = pp2;
		l1 = loc1;
		l2 = loc2;
		spawn1 = l1;
		spawn2 = l2;
		list = l;
		this.start = start;
		this.elo = elo;
		start();
	}
	
	public void callQuit (PlayerQuitEvent e){
		if (e.getPlayer() == p1){
			
			this.bedp1 = false;
			playerDeath(p1);
			
		}
		else if (e.getPlayer() == p2){
			
			this.bedp2 = false;
			playerDeath(p2);
			
		}
		else if (specs.contains(e.getPlayer())){
			e.getPlayer().teleport(plugin.getLocation("back"));
			e.getPlayer().setHealth(20);
			e.getPlayer().setFoodLevel(20);
			e.getPlayer().getInventory().clear();
		}
	}
	public int countert;
	public int sche;
	public int sche2;
	
	public void sendArenaTitle(String u , String d){
		for (Player p : getAllPlayers()){
			p.sendTitle(u, d);
		}
	}
	
	public void sendArenaActionBar(String u){
		ActionBar ab = new ActionBar (ChatColor.translateAlternateColorCodes('&', u));
		for (Player p : getAllPlayers()){
			ab.send(p);
		}
	}
	
	public void start (){
		
		respawnPlayer(p1 , true);
		respawnPlayer(p2 , true);
		
		for (Player p : Bukkit.getOnlinePlayers()){
			p.hidePlayer(p1);
			p.hidePlayer(p2);
			p1.hidePlayer(p);
			p2.hidePlayer(p);
		}
		
		p1.showPlayer(p2);
		p2.showPlayer(p1);
		
		p1.setMaxHealth(20);
		p2.setMaxHealth(20);
		p1.setHealthScale(20);
		p2.setHealthScale(20);
		
		if (elo){
			plugin.bedwars1o1.addToKey(p1.getUniqueId(), "games", 1);
			plugin.bedwars1o1.addToKey(p2.getUniqueId(), "games", 1);
		}
		
		sche = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if (running){
					endGame(null);
				}
				else{
					Bukkit.getScheduler().cancelTask(sche);
				}
			}
		}, plugin.settings.get(p2.getUniqueId()).time*60*20);
		
		countert = plugin.settings.get(p2.getUniqueId()).time*60;
		sche2 = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (running){
					countert -= 1;
					String msg = plugin.mf.getMessage("actionbar_time_remaining", true).replace("%time%", sizeUp(((int)(countert / 60)))+":"+sizeUp((countert-(((int)(countert / 60))*60))));
					sendArenaActionBar(msg);
				}
				else{
					Bukkit.getScheduler().cancelTask(sche2);
				}
			}
		}, 20 , 20);
		
		updateTabColor();
		p1.setPlayerListName(getColor(p1).toChatColor() + p1.getName());
		p2.setPlayerListName(getColor(p2).toChatColor() + p2.getName());
		
	}
	
	public String sizeUp (int i){
		
		String r = i+"";
		if (r.length() <= 1){
			r = "0"+r;
		}
		
		return r;
	}
	
	public void decline (){
		elo = false;
		Player pp1 = p1;
		Player pp2 = p2;
		endGame(null);
		pp1.sendTitle(plugin.mf.getMessage("abort_title", true) , plugin.mf.getMessage("abort_title_under", true));
		pp2.sendTitle(plugin.mf.getMessage("abort_title", true) , plugin.mf.getMessage("abort_title_under", true));
	}
	
	public void removeBedItem(Location locs){
		for (Entity e : locs.getWorld().getNearbyEntities(locs, 2, 2, 2)){
			if (e instanceof Item){
				Item i = (Item) e;
				if (i.getItemStack().getType() == Material.BED){
					e.remove();
				}
			}
		}
	}
	
	public void sendMessage(String s){
		for (Player p : specs){
			p.sendMessage(s);
		}
		p1.sendMessage(s);
		p2.sendMessage(s);
	}
	
	public static Entity getLastEntityDamager(Entity entity) {
		EntityDamageEvent event = entity.getLastDamageCause();
		if (event != null && !event.isCancelled() && (event instanceof EntityDamageByEntityEvent)) {
			Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
			if (damager instanceof Projectile) {
				Object shooter = ((Projectile) damager).getShooter();
				if (shooter != null && (shooter instanceof Entity)) return (Entity) shooter;
			}

			return damager;
		}

		return null;
	}
	
	public void callBlockBreak(BlockBreakEvent e){
		if (!blocks.contains(e.getBlock())){
			if (!(e.getBlock().getType() == Material.BED_BLOCK)){
				e.setCancelled(true);
			}
			else if (e.getBlock().getLocation().distance(l1) < e.getBlock().getLocation().distance(l2) && !(e.getPlayer() == p1)){
				bedp1 = false;
				e.getBlock().setType(Material.AIR);
				p1.getWorld().playSound(e.getBlock().getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
				sendArenaTitle("" , plugin.mf.getMessage("bed_down", true).replaceAll("%player%", getColor(p1).toChatColor()+p1.getName()));
				if (elo){
					plugin.addToStat(p2.getUniqueId(), "beds", 1);
					plugin.addPoints(p2.getUniqueId(), 8);
				}
				sendMessage(plugin.prefix+" "+plugin.mf.getMessage("bed_down", true).replaceAll("%player%", getColor(p1).toChatColor()+p1.getName()));
				removeBedItem(e.getBlock().getLocation());
			}
			else if (e.getBlock().getLocation().distance(l2) < e.getBlock().getLocation().distance(l1) && !(e.getPlayer() == p2)){
				bedp2 = false;
				e.getBlock().setType(Material.AIR);
				p1.getWorld().playSound(e.getBlock().getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
				sendArenaTitle("" , plugin.mf.getMessage("bed_down", true).replaceAll("%player%", getColor(p2).toChatColor()+p2.getName()));
				if (elo){
					plugin.addToStat(p1.getUniqueId(), "beds", 1);
					plugin.addPoints(p1.getUniqueId(), 8);
//					plugin.m.setStat(p1.getUniqueId(), "points", plugin.m.getStat(p1.getUniqueId(), "points") + 8);
//					plugin.m.setStat(p1.getUniqueId(), "beds", plugin.m.getStat(p1.getUniqueId(), "beds") + 1);
				}
				sendMessage(plugin.prefix+" "+plugin.mf.getMessage("bed_down", true).replaceAll("%player%", getColor(p2).toChatColor()+p2.getName()));
				removeBedItem(e.getBlock().getLocation());
			}
			else{
				e.setCancelled(true);
			}
		}
		else if (e.getBlock().getType() == Material.SLIME_BLOCK){
			e.setCancelled(true);
			e.getBlock().breakNaturally(new ItemStack(Material.AIR));
		}
	}
	
    private static List<Location> getRadiusBlocks(Location l, int radius)
    {
	    World w = l.getWorld();
	    int xCoord = (int) l.getX();
	    int zCoord = (int) l.getZ();
	    int YCoord = (int) l.getY();
	 
	    List<Location> tempList = new ArrayList<Location>();
	    for (int x = -radius; x <= radius; x++){
		    for (int z = -radius; z <= radius; z++){
			    tempList.add(new Location(w, xCoord + x, YCoord , zCoord + z));
		    }
	    }
	    return tempList;
    }
	
	public void spawnRescuePlatform (Player p){
		Block center = p.getLocation().getBlock().getRelative(BlockFace.DOWN , 3);
		for (Location loc : getRadiusBlocks(center.getLocation() , 1)){
			if (loc.getBlock().getType() == Material.AIR && inPreset(loc)){
				Block b = loc.getBlock();
				b.setType(Material.SLIME_BLOCK);
				this.blocks.add(b);
			}
		}
	}
	
	boolean teleporting1 = false;
	boolean teleporting2 = false;
	int tele1 = 0;
	int tele2 = 0;
	int count1 = 0;
	int count2 = 0;
	
	public void callInteract (PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			
			if (e.getPlayer().getItemInHand().getType() == Material.NETHER_STAR){
				spawnRescuePlatform(e.getPlayer());
				if (e.getPlayer().getItemInHand().getAmount() == 1){
					e.getPlayer().setItemInHand(null);
					e.getPlayer().getItemInHand().setType(Material.AIR);
				}
				else{
					e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount()-1);
				}
			}
			
			else if (e.getPlayer().getItemInHand().getType() == Material.FIREWORK){
				
				if (e.getPlayer() == p1){
					if (teleporting1){
						e.setCancelled(true);
						return;
					}
				}
				if (e.getPlayer() == p2){
					if (teleporting2){
						e.setCancelled(true);
						return;
					}
				}
				e.setCancelled(true);
				e.getPlayer().sendTitle(plugin.mf.getMessage("teleport_title", true), plugin.mf.getMessage("teleport_title_under", true).replace("%x%", "5"));
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
				
				int sch = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
					public void run() {
						int count = 0;
						if (e.getPlayer() == p1){
							count1 ++;
							count = count1;
						}
						else if (e.getPlayer() == p2){
							count2 ++;
							count = count2;
						}
						if (count == 5){
							Location loc = l1.getBlock().getRelative(BlockFace.UP , 1).getLocation();
							if (e.getPlayer() == p2){
								loc = l2.getBlock().getRelative(BlockFace.UP , 1).getLocation();
							}
							e.getPlayer().teleport(loc);
							if (e.getPlayer() == p1){
								count1 = 0;
								teleporting1 = false;
							}
							else if (e.getPlayer() == p2){
								count2 = 0;
								teleporting2 = false;
							}
							if (e.getPlayer() == p1){
								Bukkit.getScheduler().cancelTask(tele1);
							}
							else if (e.getPlayer() == p2){
								Bukkit.getScheduler().cancelTask(tele2);
							}
						}
						e.getPlayer().sendTitle(plugin.mf.getMessage("teleport_title", true), plugin.mf.getMessage("teleport_title_under", true).replace("%x%", ""+(5 - count)));
						e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
					}
				}, 20L, 20L);
				
				if (e.getPlayer() == p1){
					teleporting1 = true;
					tele1 = sch;
					count1 = 0;
				}
				
				if (e.getPlayer() == p2){
					teleporting2 = true;
					tele2 = sch;
					count2 = 0;
				}
				
				
				if (e.getPlayer().getItemInHand().getAmount() == 1){
					e.getPlayer().setItemInHand(null);
					e.getPlayer().getItemInHand().setType(Material.AIR);
				}
				else{
					e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount()-1);
				}
			}
			
		}
	}
	
	public void callDamage(EntityDamageEvent e){
		Player p = (Player) e.getEntity();
		if (p.getHealth() - e.getDamage() <= 0){
			playerDeath(p);
			e.setCancelled(true);
		}
	}
	
	public void playerDeath(Player p){
		if (p1 == p){
			if (!bedp1){
				endGame(p2);
			}
			else{
				p.getLocation().getWorld().playSound(p.getLocation(), Sound.HURT_FLESH , 1, 1);
				respawnPlayer (p , false);
			}
		}
		if (p2 == p){
			if (!bedp2){
				endGame(p1);
			}
			else{
				p.getLocation().getWorld().playSound(p.getLocation(), Sound.HURT_FLESH , 1, 1);
				respawnPlayer (p , false);
			}
		}
	}
	
	public void callMoveEvent (PlayerMoveEvent e){
		if (specs.contains(e.getPlayer())){
			if (!this.inSpecPreset(e.getPlayer().getLocation())){
				e.getPlayer().teleport(p1.getLocation());
			}
		}
		if (e.getPlayer().getLocation().getY() <= start.getY() - 80){
			playerDeath(e.getPlayer());
		}
		if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()){
			if (e.getPlayer() == p1 && teleporting1){
				Bukkit.getScheduler().cancelTask(tele1);
				p1.sendTitle(plugin.mf.getMessage("teleport_cancle", true), plugin.mf.getMessage("teleport_cancle_under", true));
				teleporting1 = false;
			}
			if (e.getPlayer() == p2 && teleporting2){
				Bukkit.getScheduler().cancelTask(tele2);
				p2.sendTitle(plugin.mf.getMessage("teleport_cancle", true), plugin.mf.getMessage("teleport_cancle_under", true));
				teleporting2 = false;
			}
		}
	}
	
	public void endGame (Player winner){
		String msg;
		if (winner == null){
			sendArenaTitle (plugin.mf.getMessage("draw_title", true) , "");
		}
		else{
			if (winner == p1){
				p2.sendTitle("" ,plugin.mf.getMessage("lost_title", true).replace("%player%", getColor(p1).toChatColor()+p1.getName()));
				p1.sendTitle("" ,plugin.mf.getMessage("won_title", true).replace("%player%", getColor(p2).toChatColor()+p2.getName()));
				if (elo){
					
					plugin.addToStat(p1.getUniqueId(), "won", 1);
					plugin.addPoints(p1.getUniqueId(), 20);
					plugin.addToStat(p2.getUniqueId(), "lost", 1);
					
				}
				sendSpecTitle(plugin.mf.getMessage("end_title_spec", true).replace("%winner%", getColor(p1).toChatColor() + p1.getName()).replace("%looser%", getColor(p2).toChatColor() + p2.getName()));
			}
			else{
				p1.sendTitle("" ,plugin.mf.getMessage("lost_title", true).replace("%player%", getColor(p2).toChatColor()+p2.getName()));
				p2.sendTitle("" ,plugin.mf.getMessage("won_title", true).replace("%player%", getColor(p1).toChatColor()+p1.getName()));
				if (elo){
					
					plugin.addToStat(p2.getUniqueId(), "won", 1);
					plugin.addPoints(p2.getUniqueId(), 20);					
					plugin.addToStat(p1.getUniqueId(), "lost", 1);

				}
				sendSpecTitle(plugin.mf.getMessage("end_title_spec", true).replace("%winner%", getColor(p2).toChatColor() + p2.getName()).replace("%looser%", getColor(p1).toChatColor() + p1.getName()));
			}
		}
		ArenaWinEvent event = new ArenaWinEvent(this , winner);
		Bukkit.getPluginManager().callEvent(event);
		stop();
	}
	
	public void respawnPlayer (Player p , boolean silent){
		p.openInventory(Bukkit.createInventory(null, 9));
		p.closeInventory();
		if (p1 == p){
			Location loc = spawn1.getBlock().getRelative(BlockFace.UP , 1).getLocation();
			loc.setX(loc.getX()+0.5);
			loc.setY(loc.getY()+1.0);
			loc.setZ(loc.getZ()+0.5);
			p.teleport(loc);
			if (!silent){
				Entity e = getLastEntityDamager(p);
				if (e == null){
					arenaMessage (plugin.mf.getMessage("died_message", true).replace("%player%", getColor(p1).toChatColor() + p1.getName()));
				}
				else if (!(e instanceof Player)){
					arenaMessage (plugin.mf.getMessage("died_message", true).replace("%player%", getColor(p1).toChatColor() + p1.getName()));
				}
				else {
					arenaMessage (plugin.mf.getMessage("killed_message", true).replace("%player%", getColor(p1).toChatColor() + p1.getName()).replace("%killer%", getColor(p2).toChatColor() + p2.getName()));
				}
			}
		}
		if (p2 == p){
			Location loc = spawn2.getBlock().getRelative(BlockFace.UP , 1).getLocation();
			loc.setX(loc.getX()+0.5);
			loc.setY(loc.getY()+0.5);
			loc.setZ(loc.getZ()+0.5);
			loc.setYaw(180);
			p.teleport(loc);
			if (!silent){
				Entity e = getLastEntityDamager(p);
				if (e == null){
					arenaMessage (plugin.mf.getMessage("died_message", true).replace("%player%", getColor(p2).toChatColor() + p2.getName()));
				}
				else if (!(e instanceof Player)){
					arenaMessage (plugin.mf.getMessage("died_message", true).replace("%player%", getColor(p2).toChatColor() + p2.getName()));
				}
				else {
					arenaMessage (plugin.mf.getMessage("killed_message", true).replace("%player%", getColor(p2).toChatColor() + p2.getName()).replace("%killer%", getColor(p1).toChatColor() + p1.getName()));
				}
			}
		}
		if (plugin.settings.get(p2.getUniqueId()).spawnprotection){
			p1.hidePlayer(p2);
			p2.hidePlayer(p1);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					p1.showPlayer(p2);
					p2.showPlayer(p1);
				}
			}, 40L);
		}
		p.setLastDamageCause(null);
		p.setFoodLevel(27);
		p.setHealth(p.getMaxHealth());
		p.setFallDistance(0);
		p.getInventory().clear();
		PlayerInventory inv = p.getInventory();
		p.playSound(p.getLocation(), Sound.HURT_FLESH, 1, 1);
		inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
	}
	
	public void arenaMessage (String msg){
		
		p1.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		p2.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		
		for (Player p : specs){
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		}
		
	}
	
	public boolean isBedCob(Block b){
		
		if (b.getRelative(BlockFace.DOWN , 1).getType().equals(Material.BED_BLOCK)){
			return true;
		}
		if (b.getRelative(BlockFace.NORTH , 1).getType().equals(Material.BED_BLOCK)){
			return true;
		}
		if (b.getRelative(BlockFace.SOUTH , 1).getType().equals(Material.BED_BLOCK)){
			return true;
		}
		if (b.getRelative(BlockFace.EAST , 1).getType().equals(Material.BED_BLOCK)){
			return true;
		}
		if (b.getRelative(BlockFace.WEST , 1).getType().equals(Material.BED_BLOCK)){
			return true;
		}
		
		return false;
	}
	
	public void callExplode (EntityExplodeEvent e){
		if (e.getEntityType() != EntityType.PRIMED_TNT){
			return;
		}
		for (Block b : new ArrayList<Block>(e.blockList())){
			if (!blocks.contains(b)){
				e.blockList().remove(b);
			}
			else{
				b.breakNaturally(new ItemStack(Material.AIR));
			}
		}
	}
	
	HashMap<UUID , Player> tntowner = new HashMap<UUID , Player>();
	
	public void callBlockPlace(BlockPlaceEvent e){
		Location loc = spawn1.getBlock().getRelative(BlockFace.UP , 1).getLocation();
		Location loc2 = spawn2.getBlock().getRelative(BlockFace.UP , 1).getLocation();
		Location loc3 = spawn1.getBlock().getRelative(BlockFace.UP , 2).getLocation();
		Location loc4 = spawn2.getBlock().getRelative(BlockFace.UP , 2).getLocation();
		if (!inPreset(e.getBlock().getLocation())){
			e.setCancelled(true);
		}
		else if (e.getBlock().getLocation().equals(loc.getBlock().getLocation()) || e.getBlock().getLocation().equals(loc2.getBlock().getLocation()) || e.getBlock().getLocation().equals(loc3.getBlock().getLocation()) || e.getBlock().getLocation().equals(loc4.getBlock().getLocation())){
			e.setCancelled(true);
		}
		else{
			if (e.getBlock().getType() == Material.TNT){
				TNTPrimed tnt = e.getBlock().getWorld().spawn(getMiddle(e.getBlock().getLocation()), TNTPrimed.class);
				tntowner.put(tnt.getUniqueId() , e.getPlayer());
				e.getBlock().setType(Material.AIR);
				return;
			}
			blocks.add(e.getBlock());
		}
		if (e.getBlock().getType() == Material.WEB && plugin.settings.get(p2.getUniqueId()).cobwebtime){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if (e.getBlock().getType() == Material.WEB){
						if (isBedCob(e.getBlock()) && plugin.settings.get(p2.getUniqueId()).cobwebtimebed){
							return;
						}
						e.getBlock().setType(Material.AIR);
					}
				}
			}, plugin.settings.get(p2.getUniqueId()).cobtime*20);
		}
	}
	
	
	public ItemStack getCustomItem (Material m , int amount , byte b , String customname, String lore , boolean enchanted){
		ItemStack stack = new ItemStack(m , amount , b);
		if (enchanted){
			stack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		}
		ItemMeta meta = stack.getItemMeta();
		ArrayList<String> list = new ArrayList<String>();
		list.add(lore);
		meta.setLore(list);
		meta.setDisplayName(customname);
		if (enchanted){
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
		}
		stack.setItemMeta(meta);
		return stack;
	}
	
	public boolean inPreset(Location loc){
		if (start.getX() <= loc.getX() && start.getZ() <= loc.getZ()){
			if (start.getX()+list.x-1 >= loc.getX() && start.getZ()+list.z-1 >= loc.getZ()){
				return true;
			}
		}
		return false;
	}
	
	public boolean inSpecPreset(Location loc){
		if (start.getX()-100 <= loc.getX() && start.getZ()-100 <= loc.getZ()){
			if (start.getX()+list.x-1+100 >= loc.getX() && start.getZ()+list.z-1+100 >= loc.getZ()){
				return true;
			}
		}
		return false;
	}
	
	public void sendSpecTitle (String title){
		for (Player p : new ArrayList<Player> (specs)){
			p.sendTitle("", title);
		}
	}
	
	public String getInvName(Inventory inv , Material m){
		
		for (ItemStack s : inv.getContents()){
			if (s.getType() == m){
				return s.getItemMeta().getDisplayName();
			}
		}
		
		return null;
	}
	
	public void stop(){
		running = false;
		list.cancelGenerator(p1 , p2);
		list.remove(start);
		plugin.am.arenas.remove(this);
		for (Block b : blocks){
			if (b != null){
				b.setType(Material.AIR);
			}
		}
		for (Entity e : Villagers){
			e.remove();
		}
		if (plugin.useHolographicDisplays){
			try{
				for (Object h : holograms){
					Hologram hh = (Hologram) h;
					hh.delete();
				}
			}catch(Exception ex){
				
			}
		}
		p1.getEnderChest().clear();
		p2.getEnderChest().clear();
		Bukkit.getScheduler().cancelTask(sch1);
		Bukkit.getScheduler().cancelTask(sch2);
		Bukkit.getScheduler().cancelTask(sch3);
		p1.teleport(plugin.getLocation("back"));
		p1.setHealth(p1.getMaxHealth());
		p1.setFoodLevel(20);
		p1.getInventory().clear();
		p1.getEnderChest().clear();
		PlayerInventory inv = p1.getInventory();
		inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
		if (elo){
			plugin.addPoints(p1.getUniqueId(), 5);
			plugin.addPoints(p2.getUniqueId(), 5);
//			plugin.m.setStat(p1.getUniqueId(), "points", plugin.m.getStat(p1.getUniqueId(), "points") + 2);
//			plugin.m.setStat(p2.getUniqueId(), "points", plugin.m.getStat(p2.getUniqueId(), "points") + 2);
		}
		p2.teleport(plugin.getLocation("back"));
		p2.setHealth(p2.getMaxHealth());
		p2.setFoodLevel(20);
 		p2.getInventory().clear();
		p2.getEnderChest().clear();
		inv = p2.getInventory();
		inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
		plugin.giveLobbyItem(p1);
		plugin.giveLobbyItem(p2);
		
		for (Player p : new ArrayList<Player> (specs)){
			p.setGameMode(GameMode.SURVIVAL);
			p.teleport(plugin.getLocation("back"));
			plugin.giveLobbyItem(p);
			specs.remove(p);
			p.setGameMode(GameMode.SURVIVAL);
		}
		
		for (int a : new ArrayList<Integer>(plugin.am.arenas.keySet())){
			if (plugin.am.arenas.get(a) == this){
				plugin.am.arenas.remove(a);
			}
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (plugin.am.searchPlayer(p) == null){
				p.showPlayer(p1);
				p.showPlayer(p2);
				p1.showPlayer(p);
				p2.showPlayer(p);
			}
			ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
			p.setPlayerListName(p.getName());
		}
		
        ArenaStopEvent event = new ArenaStopEvent(this , p1 , p2);
        Bukkit.getServer().getPluginManager().callEvent(event);
		
		p1 = null;
		p2 = null;
	}
}
