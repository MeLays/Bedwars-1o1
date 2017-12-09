/*
 * Copyright (c) Philipp Seelos 2017 All rights reserved.
 *
 * You are not allowed to use or edit this piece of software and ALL its including class/java files 
 * Without explicit permission of the author of this code (Philipp Seelos)
 * The author (Philipp Seelos) has the permission to deny the usage of this software at any time.
 */ 

package de.melays.bw1o1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.statsAPI.Channel;
import de.melays.statsAPI.RankUpdater;
import de.melays.statsAPI.StatsAPI;

public class main extends JavaPlugin implements Listener{
	
	public HashMap<Player , LocationMarker> markers = new HashMap<Player , LocationMarker>();
	public HashMap<Player , ChallengePlayer> cp = new HashMap<Player , ChallengePlayer>();
	public HashMap<Player , String> arenas = new HashMap<Player , String>();
	public HashMap<Player , Boolean> elo = new HashMap<Player , Boolean>();
	public PresetManager pm;
	public ArenaManager am;
	public String prefix = ChatColor.translateAlternateColorCodes('&', "&8&l(&bBW1o1&8&l) &7");
	public ArrayList<String> presets = new ArrayList<String>();
	public ArrayList<Player> dontmove = new ArrayList<Player>();
	
	public StatsAPI stats;
	public Channel bedwars1o1;
	public Channel BW1o1Settings;
	public Channel global;
	public Channel namedb;
	public RankUpdater ranker;
	
	public MessageFetcher mf;
	
	public void addToStat(UUID u , String key , int amount){
		
		bedwars1o1.addToKey(u, key, amount);
		
	}
	
	public void addPoints(UUID u , int amount){
		
		bedwars1o1.addToKey(u, "points", amount);
		global.addToKey(u, "points", amount);
		global.addToKey(u, "coins", amount);
	}
	
	@Override
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new BWShop(this), this);
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("gameworld", "bw");
		getConfig().addDefault("lobbyitem", true);
		getConfig().addDefault("lobbyserver", "Lobby-1");
		getConfig().addDefault("speedgenerator", 7);
		getConfig().addDefault("generator", 14);
		saveConfig();
		pm = new PresetManager(this);
		am = new ArenaManager(this);
		
		
		
		mf = new MessageFetcher(this);
		mf.getMessageFetcher().options().copyDefaults(true);
		mf.saveMessageFile();

		stats = (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
		bedwars1o1 = stats.hookChannel(this, "bedwars1o1");
		global = stats.hookChannel(this, "global");
		ranker = new RankUpdater(bedwars1o1);
		BW1o1Settings = stats.hookChannel(this, "BW1o1Settings");
		
		prefix = mf.getMessage("prefix", true);
		
		for (Player p : Bukkit.getOnlinePlayers()){
			settings.put(p.getUniqueId(), new Settings(p , this));
			p.teleport(getLocation("back"));
			p.getInventory().clear();
			elo.put(p, true);
			this.giveLobbyItem(p);
		}
		
		//Load Presets
		presets = (ArrayList<String>) getConfig().getStringList("Presets");
		if (presets == null){
			presets = new ArrayList<String>();
		}
		for (String t : new ArrayList<String>(presets)){
			if (!getConfig().getBoolean("Preset."+t+".enabled")){
				presets.remove(t);
			}
		}
		
		int refreshspeed = randInt(400, 1000);
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				try{
					ranker.updateRank("points");
				}
				catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}, 0 , refreshspeed);
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				try{
					updateAllSigns();
				}
				catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}, 40 , refreshspeed);
		
		boolean loaded = false;
		for(World w: Bukkit.getServer().getWorlds())
		{
		  if(w.getName().equals(getConfig().getString("gameworld")))
		  {
		    loaded = true;
		    break;
		  }
		}
		if(!loaded)
		{
		  System.out.println("[BW1o1] Gameworld not found! Shutting plugin off!");
		  Bukkit.getPluginManager().disablePlugin(this);
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
			new PlaceHolderAPI(this).hook();
			System.out.println("[BW1o1] PlaceholderAPI hooked!");
		}
		
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			getLogger().severe("[BW1o1] *** HolographicDisplays is not installed or not enabled. ***");
		}
		useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
		
	}
	
	public boolean useHolographicDisplays;
	
	@EventHandler
	public void Explode(EntityExplodeEvent e){
		for (Arena a : am.arenas.values()){
			a.callExplode(e);
		}
	}
	
	@EventHandler
	public void moveE (PlayerMoveEvent e){
		if (dontmove.contains(e.getPlayer()) && am.searchPlayer(e.getPlayer()) != null){
			if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()){
				e.getPlayer().teleport(e.getFrom());
			}
		}
		if (am.searchPlayer(e.getPlayer()) == null && e.getTo().getY() < 0){
			e.getPlayer().teleport(getLocation("back"));
		}
	}
	
	@Override
	public void onDisable(){
		try{
			for (int s : new ArrayList<Integer> (am.arenas.keySet())){
				am.arenas.get(s).stop();
			}
			for (Player p : Bukkit.getOnlinePlayers()){
				settings.get(p.getUniqueId()).saveSettings();
			}
		}catch(Exception ex){
			
		}
	}
	
	@EventHandler
	public void PlayerInteractEvent (PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK){
			if (e.getPlayer().hasPermission("bw.setup") && e.getPlayer().getGameMode() == GameMode.CREATIVE){
			
			}
			else{
				if (am.searchPlayer(e.getPlayer()) == null){
					e.setCancelled(true);
				}
			}
		}
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public void openMapGui(Player p){
		if (!arenas.containsKey(p)){
			arenas.put(p, presets.get(randInt(0,presets.size()-1)));
		}
		int size = (presets.size() / 9) +1;
		Inventory inv = Bukkit.createInventory(null, size*9 , mf.getMessage("gui_map", false));
		for (String s : presets){
			boolean ench = false;
			if (arenas.containsKey(p)){
				if (s.equals(arenas.get(p))){
					ench = true;
				}
			}
			inv.addItem(getCustomItem(pm.getPresetItem(s) , 1 , (byte) 0 , ChatColor.GREEN + s ,  mf.getMessage("gui_map_click", false) , ench));
		}
		p.openInventory(inv);
	}
	
	  public void fillPanes (Inventory inv){
			for (int i = 0 ; i < inv.getSize() ; i++){
				
				if (inv.getItem(i) == null){
					ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE , 1, (byte)15);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName(" ");
					stack.setItemMeta(meta);
					inv.setItem(i, stack);
					
				}
				if (inv.getItem(i).getType().equals(Material.AIR)){
					ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE , 1, (byte)15);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName(" ");
					stack.setItemMeta(meta);
					inv.setItem(i, stack);
				}
				
				
			}
			
		}
	
	public void openRunning(Player p){
		
		int size = (am.arenas.size() / 2) +2;
		Inventory inv = Bukkit.createInventory(null, size*9 , mf.getMessage("gui_running", false));
		
		for (int i = 0 ; i < size ; i++){
			inv.setItem(0 + (9*i), getCustomItem(Material.STAINED_GLASS_PANE , 1 , (byte) 0 , " " , "" , false));
			inv.setItem(3 + (9*i), getCustomItem(Material.STAINED_GLASS_PANE , 1 , (byte) 0 , " " , "" , false));
			inv.setItem(4 + (9*i), getCustomItem(Material.STAINED_GLASS_PANE , 1 , (byte) 15 , " " , "" , false));
			inv.setItem(5 + (9*i), getCustomItem(Material.STAINED_GLASS_PANE , 1 , (byte) 0 , " " , "" , false));
			inv.setItem(8 + (9*i), getCustomItem(Material.STAINED_GLASS_PANE , 1 , (byte) 0 , " " , "" , false));
		}
		
		for (int s : am.arenas.keySet()){
			Arena a = am.arenas.get(s);
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(mf.getMessage("gui_running_id", false)+": "+s);
			inv.addItem(getPlayerSkull(a.p1 , ChatColor.BLUE+a.p1.getName() , lore));
			inv.addItem(getPlayerSkull(a.p2 , ChatColor.RED+a.p2.getName() , lore));
		}
		fillPanes(inv);
		p.openInventory(inv);
	}
	
	public ItemStack getPlayerSkull (Player p , String name , ArrayList<String> lore){
		SkullMeta  meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

		meta.setOwner(p.getName());
		
		meta.setDisplayName(name);
		
		meta.setLore(lore);
		
		ItemStack stack = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);

		stack.setItemMeta(meta);
		
		return stack;
	}
	
	@EventHandler
	public void signchange (SignChangeEvent e){
		if (e.getLine(0).equals("[Rank]")){
			UUID u = UUID.randomUUID();
			this.writeLocation("signs."+u, e.getBlock().getLocation());
			getConfig().set("signs."+u+".rank", Integer.parseInt(e.getLine(1)));
			saveConfig();
			updateAllSigns();
		}
	}
	
	 @SuppressWarnings("deprecation")
	 public void spawnHeadBlock(Location loc, String name, byte face)
	 {
		  
		 Block b = loc.getBlock();
		 b.setTypeIdAndData(Material.SKULL.getId(), face, true);
		 Skull skull = (Skull) b.getState();
		 skull.setSkullType(SkullType.PLAYER);
		 try{
			 skull.setOwner(name);
		 }catch(Exception ex){}
		 skull.update(true);
	  
	 }
	
	public void updateSign(UUID u){
		try{
			Location loc = this.getLocation("signs."+u);
			int rank = getConfig().getInt("signs."+u+".rank");
			UUID uu = ranker.getRank(rank);
			OfflinePlayer player = Bukkit.getOfflinePlayer(uu);
			
			Sign sign = (Sign) loc.getBlock().getState();
			
			String name = player.getName();
			
			if (name == null || name.equals("")){
				name = stats.getNameFromUUID(uu);
			}
			
			if (name == null){
				name = "";
			}
			
			Block b1 = loc.getBlock().getRelative(BlockFace.UP);
			spawnHeadBlock(b1.getLocation() , name , sign.getData().getData());
			int points = this.bedwars1o1.getKey(uu, "points");
			
			sign.setLine(0, mf.getMessage("ranksign_line1", true).replace("%color%", getRankColor(rank)+"").replace("%rank%", rank+"").replace("%name%", name).replace("%points%" , points+""));
			sign.setLine(1, mf.getMessage("ranksign_line2", true).replace("%color%", getRankColor(rank)+"").replace("%rank%", rank+"").replace("%name%", name).replace("%points%" , points+""));
			sign.setLine(2, mf.getMessage("ranksign_line3", true).replace("%color%", getRankColor(rank)+"").replace("%rank%", rank+"").replace("%name%", name).replace("%points%" , points+""));
			sign.setLine(3, mf.getMessage("ranksign_line4", true).replace("%color%", getRankColor(rank)+"").replace("%rank%", rank+"").replace("%name%", name).replace("%points%" , points+""));
			sign.update(true);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public ChatColor getRankColor (int i){
		if (i == 1){
			return ChatColor.GOLD;
		}
		if (i == 2){
			return ChatColor.GRAY;
		}
		if (i == 3){
			return ChatColor.RED;
		}
		return ChatColor.DARK_GRAY;
	}
	
	public void updateAllSigns (){
		try{
			Set<String> signs = getConfig().getConfigurationSection("signs").getKeys(false);
			for (String s : signs){
				try{
					updateSign(UUID.fromString(s));
				}
				catch(Exception ex){
					
				}
			}
		}
		catch(Exception ex){
			
		}
	}
	
	@EventHandler
	public void playerleaveevent (PlayerQuitEvent e){
		if (am.searchPlayer(e.getPlayer()) != null){
			Arena a = am.searchPlayer(e.getPlayer());
			
			a.callQuit(e);
			
		}
		settings.get(e.getPlayer().getUniqueId()).saveSettings();
	}
	
	@EventHandler
	public void gmChange (PlayerGameModeChangeEvent e){
		Arena a = am.searchPlayer(e.getPlayer());
		if (a != null && a.specs.contains(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void invClick (InventoryClickEvent e){
		try{
			if (am.searchPlayer((Player)e.getWhoClicked()) == null && e.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL)){
				e.setCancelled(true);
			}
			if (e.getClickedInventory().getType() == InventoryType.CRAFTING){
				e.setCursor(new ItemStack (Material.AIR));
			}
			if (e.getClickedInventory().getName().equals(mf.getMessage("gui_map", false))){
				
				if (e.getCurrentItem().getType() != Material.AIR){
					
					e.setCancelled(true);
					String map = e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.GREEN +"", "");
					if (presets.contains(map)){
						
						arenas.put((Player) e.getWhoClicked(), map);
						e.setCancelled(true);
						openMapGui((Player)e.getWhoClicked());
						((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.WOOD_CLICK, 1, 1);
					
						
					}
					
				}
				e.setCancelled(true);
				
			}
			else if (e.getClickedInventory().getName().equals(mf.getMessage("gui_running", false))){
				
				if (e.getCurrentItem().getType() != Material.AIR && e.getCurrentItem().getType() != Material.STAINED_GLASS_PANE){
						Arena a = am.searchPlayer(Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.RED+"", "").replace(ChatColor.BLUE+"", "")));
						if (a != null){
							a.addSpec((Player)e.getWhoClicked());
						}
						else{
							e.getWhoClicked().sendMessage(mf.getMessage("game_not_found", true));
						}
				}
				e.setCancelled(true);
				
			}
			try{
				settings.get(e.getWhoClicked().getUniqueId()).callClick(e);
			}
			catch(Exception ex){
				
			}
		}
		catch(Exception ex){
			
		}
	}
	
	@EventHandler
	public void drop (PlayerDropItemEvent e){
		if (this.am.searchPlayer(e.getPlayer()) == null){
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void drop (CraftItemEvent e){
		 e.setCancelled(true);
	}
	
	@EventHandler
	public void InteractEEvent (PlayerInteractEntityEvent e){
		final Player p = e.getPlayer();
		if (e.getRightClicked() instanceof Player){
			Player clicked = (Player) e.getRightClicked();
			if (p.getItemInHand().getType() == Material.IRON_SWORD){
				if (p.getItemInHand().getItemMeta().getDisplayName().equals(challengerItem().getItemMeta().getDisplayName())){
					if (cp.containsKey(p)){
						for (Challenge c : cp.get(p).challenges){
							if (c.requester == clicked){
								cp.get(p).challenges.remove(c);
								p.sendMessage(mf.getMessage("challenge_rejected", true).replace("%player%", clicked.getName()));
								clicked.sendMessage(mf.getMessage("challenge_rejected_challenger", true).replace("%player%", p.getName()));
								e.setCancelled(true);
								this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									public void run() {
										p.closeInventory();
									}
								}, 3L);
							}
						}
					}
				}
			}
		}
		
	}
	
	public HashMap<UUID,Settings> settings = new HashMap<UUID,Settings>();
	
	@EventHandler
	public void onEvent (PlayerInteractEvent e){
		Arena a = am.searchPlayer(e.getPlayer());
		if (a != null){
			a.callInteract(e);
		}
	}
	
	@EventHandler
	public void InteractEvent (PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && am.searchPlayer(e.getPlayer()) == null){
			Player p = e.getPlayer();
			if (p.getItemInHand().getType() == Material.IRON_SWORD){
				if (p.getItemInHand().getItemMeta().getDisplayName().equals(challengerItem().getItemMeta().getDisplayName())){
					try{
						openMapGui (p);
					}
					catch (Exception ex){
						p.sendMessage(mf.getMessage("no_presets", true));
					}
				}
			}
			else if (p.getItemInHand().getType() == Material.BOOK){
				if (p.getItemInHand().getItemMeta().getDisplayName().equals(mf.getMessage("gui_running_item", false))){
					openRunning (e.getPlayer());
				}
			}
			else if (p.getItemInHand().getType() == Material.REDSTONE_COMPARATOR){
				settings.get(e.getPlayer().getUniqueId()).openInventory();
			}
			else if (p.getItemInHand().getType() == Material.STAINED_CLAY){
				settings.get(e.getPlayer().getUniqueId()).openColorInventory();
			}
			else if (p.getItemInHand().getType() == Material.SUGAR || p.getItemInHand().getType() == Material.GLOWSTONE_DUST && am.searchPlayer(p) == null){
				if (!elo.containsKey(p)){
					elo.put(p, true);
				}
				if (elo.get(p)){
					elo.put(p, false);
				}
				else{
					elo.put(p, true);
				}
				p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1, 1);
				if (this.elo.get(p)){
					p.getInventory().setItem(2, getCustomItem(Material.GLOWSTONE_DUST , 1 , (byte) 0 , mf.getMessage("statstoggle_item_on", false) , "" , true));
				}
				else{
					p.getInventory().setItem(2, getCustomItem(Material.SUGAR , 1 , (byte) 0 ,  mf.getMessage("statstoggle_item_off", false) , "" , true));
				}
			}
			else if (p.getItemInHand().getType() == Material.SLIME_BALL && am.searchPlayer(p) == null){
				getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(getConfig().getString("lobbyserver"));
				p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
			}
		}
	}
	
	public ItemStack challengerItem (){
		
		ItemStack stack = getCustomItem (Material.IRON_SWORD , 1 , (byte) 0 , mf.getMessage("challenger_item", false) , mf.getMessage("challenger_item_lore", false) , true);
		
		return stack;
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
		if (lore.equals("")){
			meta.setLore(null);
		}
		meta.setDisplayName(customname);
		if (enchanted){
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
		}
		stack.setItemMeta(meta);
		return stack;
	}
	
	ArrayList<Player> wait = new ArrayList<Player>();
	
	@EventHandler
	public void pvpEvent(EntityDamageByEntityEvent e){
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			final Player hitter = (Player)e.getDamager();
			final Player p = (Player) e.getEntity();
			if (am.searchPlayer(hitter) == null && am.searchPlayer(p) == null){
				if (hitter.getItemInHand().getType() == Material.IRON_SWORD){
					if (hitter.getItemInHand().getItemMeta().getDisplayName().equals(challengerItem().getItemMeta().getDisplayName())){
						e.setCancelled(true);
						if (!wait.contains(hitter)){
							if (!cp.containsKey(p)){
								cp.put(p, new ChallengePlayer(this , p));
							}
							
							cp.get(p).callHit(hitter);
							
							wait.add(hitter);
							
							this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
								public void run() {
									if (wait.contains(hitter)){
										wait.remove(hitter);
									}
								}
							} , 100L);
							
						}
						else{
							
							hitter.sendMessage(mf.getMessage("challenge_cooldown", true));
							
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract (PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.getPlayer().isSneaking()){
			if (e.getClickedBlock().getType() == Material.BED_BLOCK){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void PlayerJoinEvent (PlayerJoinEvent e){
		Player p = e.getPlayer();
		p.setMaxHealth(20);
		try{
			p.teleport(getLocation("back"));
		}catch(Exception ex){
			
		}
		p.setGameMode(GameMode.SURVIVAL);
		p.getInventory().clear();
		PlayerInventory inv = p.getInventory();
		inv.setArmorContents(new ItemStack[inv.getArmorContents().length]);
		settings.put(p.getUniqueId(), new Settings(p , this));
		for (Player pp : am.getAllPlayers()){
			pp.hidePlayer(p);
			p.hidePlayer(pp);
		}
		
		for (Player pp : am.getAllSpecs()){
			pp.hidePlayer(p);
			p.hidePlayer(pp);
		}
		elo.put(p, true);
		this.giveLobbyItem(p);
		e.setJoinMessage(null);
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			  public void run() {
				  p.teleport(getLocation("back"));
			  }
		}, 5L);
	}
	
	@EventHandler
	public void BlockBreakEvent (BlockBreakEvent e){
		Player p = e.getPlayer();
		Arena a = am.searchPlayer(p);
		if (a != null){
			a.callBlockBreak(e);
		}
	}
	
	@EventHandler
	public void BlockPlaceEvent (BlockPlaceEvent e){
		Player p = e.getPlayer();
		Arena a = am.searchPlayer(p);
		if (a != null){
			a.callBlockPlace(e);
		}
	}
	
	@EventHandler
	public void move (PlayerMoveEvent e){
		Player p = e.getPlayer();
		Arena a = am.searchPlayer(p);
		if (a != null){
			a.callMoveEvent(e);
		}
	}
	
	@EventHandler
	public void food (FoodLevelChangeEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			Arena a = am.searchPlayer(p);
			if (a == null){
				p.setFoodLevel(20);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerdmg (EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			Arena a = am.searchPlayer(p);
			if (a == null){
				e.setCancelled(true);
			}
			else{
				a.callDamage(e);
			}	
		}
		if (e.getEntity() instanceof Villager){
			for (Arena a : am.arenas.values()){
				if (a.Villagers.contains(e.getEntity())){
					e.setCancelled(true);
				}
			}
		}
	}
	
	
	@EventHandler
	public void PlayerDeathEvent (EntityDeathEvent e){
		if (e.getEntity() instanceof Player){
			((Player)e.getEntity()).spigot().respawn();
		}
	}
	
	@EventHandler
	public void PlayerChatEvent (AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		Arena a = am.searchPlayer(p);
		if (a == null){
			
			for (Player receiver : p.getWorld().getPlayers()){
				receiver.sendMessage(p.getDisplayName()+ChatColor.DARK_GRAY+" > "+ChatColor.GRAY+e.getMessage());
				e.setCancelled(true);
			}
			
		}
		else{
			if (!a.specs.contains(p)){
				a.arenaMessage(a.getColor(p).toChatColor()+p.getName()+ChatColor.DARK_GRAY+" > "+ChatColor.GRAY+e.getMessage());
				e.setCancelled(true);
			}
			else{
				for (Player s : a.specs){
					s.sendMessage(ChatColor.DARK_GRAY+p.getName()+ChatColor.GRAY+" > "+ChatColor.DARK_GRAY+e.getMessage());
				}
				e.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler
	public void interactEvent (PlayerInteractEvent e){
		if (e.getPlayer().hasPermission("bw.setup")){
			if (e.getPlayer().getItemInHand().getType() == Material.BLAZE_ROD && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
				
				if (!markers.containsKey(e.getPlayer())){
					markers.put(e.getPlayer(), new LocationMarker(e.getPlayer()));
				}
				
				if (e.getAction() == Action.LEFT_CLICK_BLOCK){
					markers.get(e.getPlayer()).setLeft(e.getClickedBlock().getLocation());
					e.getPlayer().sendMessage(prefix + ChatColor.LIGHT_PURPLE + "Set Location LEFTCLICK");
				}
				else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
					markers.get(e.getPlayer()).setRight(e.getClickedBlock().getLocation());
					e.getPlayer().sendMessage(prefix + ChatColor.LIGHT_PURPLE + "Set Location RIGHTCLICK");
				}
				e.setCancelled(true);
				
			}
		}
	}
	
	public void giveLobbyItem (Player p){
		p.getInventory().clear();
		p.getInventory().addItem(this.challengerItem());
		p.getInventory().addItem(getCustomItem(Material.BOOK , 1 , (byte) 0 , mf.getMessage("gui_running_item", false) , mf.getMessage("gui_running_item_lore", true) , false));
		if (this.elo.get(p)){
			p.getInventory().setItem(2, getCustomItem(Material.GLOWSTONE_DUST , 1 , (byte) 0 , mf.getMessage("statstoggle_item_on", false) , "" , true));
		}
		else{
			p.getInventory().setItem(2, getCustomItem(Material.SUGAR , 1 , (byte) 0 ,  mf.getMessage("statstoggle_item_off", false) , "" , true));
		}
		p.getInventory().setItem(7,getCustomItem(Material.REDSTONE_COMPARATOR , 1 , (byte) 0 ,  ChatColor.GRAY + "Einstellungen" , "" , true));
		p.getInventory().setItem(6, new ItemBuilder(Material.STAINED_CLAY).setDurability(settings.get(p.getUniqueId()).primary.toByte()).setName(ChatColor.GRAY+"Farbauswahl").toItemStack());
		if (getConfig().getBoolean("lobbyitem")){
			p.getInventory().setItem(8,getCustomItem(Material.SLIME_BALL , 1 , (byte) 0 ,  mf.getMessage("lobbyitem", false) , "" , false));
		}
		
	}
	
	public void writeLocation (String path , Location loc){
		getConfig().set(path+".x" , loc.getX());
		getConfig().set(path+".y" , loc.getY());
		getConfig().set(path+".z" , loc.getZ());
		getConfig().set(path+".yaw" , loc.getYaw());
		getConfig().set(path+".pitch" , loc.getPitch());
		getConfig().set(path+".world" , loc.getWorld().getName());
		saveConfig();
	}
	
	public Location getLocation (String path){
		double x = getConfig().getDouble(path+".x");
		double y = getConfig().getDouble(path+".y");
		double z = getConfig().getDouble(path+".z");
		float yaw = (float)getConfig().getDouble(path+".yaw");
		float pitch = (float)getConfig().getDouble(path+".pitch");
		String world = getConfig().getString(path+".world");
		return new Location (Bukkit.getWorld(world) , x ,y  ,z , yaw , pitch);
	}
	
	ArrayList<Player> cooldown = new ArrayList<Player>();
	
	public void sendStatsMessage(Player p ,  UUID u){
		
		int games = this.bedwars1o1.getKey(u, "games");
		int wins = this.bedwars1o1.getKey(u, "won");
		int looses = this.bedwars1o1.getKey(u, "lost");
		double wl = 0.0;
		try{
			wl = wins/looses;
		}
		catch (Exception ex){

		}
		int beds = this.bedwars1o1.getKey(u, "beds");
		int points = this.bedwars1o1.getKey(u, "points");
		String name = stats.getNameFromUUID(u);
		
		for (String s : mf.getMessageFetcher().getStringList("stats")){
			s = s.replace("%player%", name);
			s = s.replace("%games%", games+"");
			s = s.replace("%wins%", wins+"");
			s = s.replace("%looses%", looses+"");
			s = s.replace("%wl%", wl+"");
			s = s.replace("%beds%", beds+"");
			s = s.replace("%points%", points+"");
			s = s.replace("%prefix%", mf.getMessage("prefix", false));
			s = ChatColor.translateAlternateColorCodes('&', s);
			p.sendMessage(s);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("You cant do this . console!");
			return true;
		}
		final Player p = (Player) sender;
		
		
		if(cmd.getName().equalsIgnoreCase("stats")){
			if (args.length == 0){
				if (cooldown.contains(p)){
					p.sendMessage(mf.getMessage("stats_cooldown", true));
					return true;
				}
				sendStatsMessage(p, p.getUniqueId());
				cooldown.add(p);
				this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						cooldown.remove(p);
					}
				}, 200L);
			}
			else{
				
				if (cooldown.contains(p)){
					p.sendMessage(mf.getMessage("stats_cooldown", true));
					return true;
				}
				
				if (args[0].equalsIgnoreCase("bw1o1") && args.length == 1){
					p.performCommand("stats");
					return true;
				}
				else if (args[0].equalsIgnoreCase("bw1o1")){
					p.performCommand("stats "+args[1]);
					return true;
				}
				try{
					OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[0]);
					if (p2.isOnline() || p2.hasPlayedBefore()){
						sendStatsMessage(p, p2.getUniqueId());
						cooldown.add(p);
						this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
							public void run() {
								cooldown.remove(p);
							}
						}, 200L);
					}
				}
				catch (Exception ex){
					p.sendMessage(mf.getMessage("stats_not_found", true));
					return true;
				}
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("bw") && args.length >= 1){
			
			if (args[0].equalsIgnoreCase("createpreset")){
				
				if (!p.hasPermission("bw.setup")){
					p.sendMessage(mf.getMessage("no_perms", true));
					return true;
				}
				
				if (args.length != 3){
					p.sendMessage(prefix + "USAGE: /bw createpreset <Name> <Menu-Item>");
				}
				
				if (markers.get(p).complete()){
					Preset pr = new Preset (this , markers.get(p).locl , markers.get(p).locr);
					ArrayList<String>s = (ArrayList<String>) getConfig().getStringList("Presets");
					if (s == null){
						s = new ArrayList<String>();
					}
					if (!s.contains(args[1])){
						s.add(args[1]);
					}
					getConfig().set("Presets" , s);
					String item = args[2].toUpperCase();
					Material m = Material.getMaterial(item);
					if (m == null){
						m = Material.PAPER;
					}
					getConfig().set("Preset."+args[1]+".item" , m.toString());
					getConfig().set("Preset."+args[1]+".enabled" , false);
					writeLocation ("Preset."+args[1]+".pos1" , pr.pos1);
					writeLocation ("Preset."+args[1]+".pos2" , pr.pos2);
					markers.get(p).locl = null;
					markers.get(p).locr = null;
					p.sendMessage(prefix + " Preset gespeichert !");
					Preset preset = pm.getPreset(args[1]);
					if (preset != null){
						PresetList plist = preset.generateGenerateList();
						p.sendMessage(prefix+" Generiere: "+plist.list.size()+" Blöcke!");
						preset.generateGenerateList().generatePreset(preset.pos1 , null , null);
						p.sendMessage(prefix+" Fertig!");
						p.sendMessage(prefix+" Das Preset ist noch nicht aktiviert! Lade es mit /bw enable!");
					}
					else{
						p.sendMessage(prefix+ChatColor.RED+" Das Preset konnte nicht generiert werden!");
					}
					
				}
				else{
					p.sendMessage(prefix + ChatColor.RED + " Du musst beide Markierungen setzen!");
				}
				
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("setlobby") && args.length == 1){
				
				if (!p.hasPermission("bw.setup")){
					p.sendMessage(mf.getMessage("no_perms", true));
					return true;
				}
				
				writeLocation("back" , p.getLocation());
				p.sendMessage(prefix +  " The Lobby-Location has been set successfully!");
				
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("enable") && args.length == 2){
				
				if (!p.hasPermission("bw.setup")){
					p.sendMessage(mf.getMessage("no_perms", true));
					return true;
				}
				
				if (getConfig().getString("Preset."+args[1]+".item") == null){
					p.sendMessage(prefix + " Dieses Preset exisitert nicht!");
				}
				
				presets.add(args[1]);
				getConfig().set("Preset."+args[1]+".enabled", true);
				p.sendMessage(prefix + " Das Preset wurde aktiviert und geladen!");
				saveConfig();
				
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("generatepreset") && args.length == 2){
				
				if (!p.hasPermission("bw.setup")){
					p.sendMessage(mf.getMessage("no_perms", true));
					return true;
				}
				
				Preset preset = pm.getPreset(args[1]);
				if (preset != null){
					PresetList plist = preset.generateGenerateList();
					p.sendMessage(prefix+"Generating: "+plist.list.size()+" Blocks");
					preset.generateGenerateList().generatePreset(p.getLocation() , null , null);
					p.sendMessage(prefix+"Finished");
				}
				else{
					p.sendMessage(prefix+ChatColor.RED+" Error loading the preset!");
				}
				
			}
			else if (args[0].equalsIgnoreCase("start") && args.length == 4){
				
				if (!p.hasPermission("bw.setup")){
					p.sendMessage(mf.getMessage("no_perms", true));
					return true;
				}
				
				Preset preset = pm.getPreset(args[1]);
				if (preset != null){
					int a = am.createArena(Bukkit.getPlayer(args[2]), Bukkit.getPlayer(args[3]), preset , false);
					p.sendMessage(prefix+" Ein Spiel wurde auf dem Preset "+args[1]+" gestartet. Die ID lautet "+a);
				}
				else{
					p.sendMessage(prefix+ChatColor.RED+" Fehler beim laden des Presets!");
				}
				
			}
			
			else if (args[0].equalsIgnoreCase("stop") && args.length == 2){
				
				if (!p.hasPermission("bw.setup")){
					p.sendMessage(mf.getMessage("no_perms", true));
					return true;
				}
				try{
					am.arenas.get(Integer.parseInt(args[1])).stop();
					p.sendMessage(prefix+ChatColor.RED+" Arena "+args[1]+" wurde gestoppt!");
				}
				catch(Exception ex){
					p.sendMessage(prefix+ChatColor.RED+" Arena "+args[1] + "konnte nicht gestoppt werden!");
				}
				
			}
			
			
			else{
				p.sendMessage(mf.getMessage("unknown_command", true));
			}
			
		}
		else if (cmd.getName().equalsIgnoreCase("bw")){
			p.sendMessage(prefix + "BW 1o1 Plugin Version 1.0 Alpha by MeLays");
			if (p.hasPermission("bw.setup")){
				p.sendMessage(prefix + "/bw Sub-Commands:");
				p.sendMessage(prefix + "   createpreset <Name> <Item>");
				p.sendMessage(prefix + "   enable <Name>");
				p.sendMessage(prefix + "   setlobby");
				p.sendMessage(prefix + "   start <Preset> <Player> <Player>");
				p.sendMessage(prefix + "   generatepreset <Preset>");
				p.sendMessage(prefix + "   stop <ID>");
			}
			p.sendMessage(prefix + "/surrender , /leave");
			p.sendMessage(prefix + "/requests");
		}
		else if(cmd.getName().equalsIgnoreCase("requests")){
			if (cp.containsKey(p)){
				p.sendMessage(mf.getMessage("requests_command", true));
				for (Challenge c : cp.get(p).challenges){
					p.sendMessage(mf.getMessage("request", true).replace("%player%", c.requester.getName()).replace("%map%", c.preset));
				}
			}
			else{
				p.sendMessage(mf.getMessage("no_requests", true));
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("surrender") || cmd.getName().equalsIgnoreCase("leave")){
			
			Arena a = am.searchPlayer(p);
			if (a != null){
				if (a.specs.contains(p)){
					p.hidePlayer(a.p1);
					p.hidePlayer(a.p2);
					a.specs.remove(p);
					giveLobbyItem(p);
					p.setGameMode(GameMode.SURVIVAL);
					p.teleport(getLocation("back"));
				}
				else{
					if (p == a.p1){
						a.bedp1 = false;
						a.playerDeath(p);
					}
					else if (p == a.p2){
						a.bedp2 = false;
						a.playerDeath(p);
					}
				}
			}
			else{
				p.sendMessage(mf.getMessage("not_ingame", true));
			}

			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("cancle")){
			
			Arena a = am.searchPlayer(p);
			if (a != null){
				if (settings.get(a.p2.getUniqueId()).time*60 - a.countert < 30 && a.elo && p.getUniqueId().equals(a.p1.getUniqueId())){
					a.decline();
					return true;
				}
				p.sendMessage(prefix+ ChatColor.RED + "Du kannst diese Runde nicht abbrechen!");
			}
			else{
				p.sendMessage(mf.getMessage("not_ingame", true));
			}

			return true;
		}
		
		else if(cmd.getName().equalsIgnoreCase("accept")){
			
			if (args.length != 1){
				p.sendMessage(prefix+ " /accept <Spieler>");
				return true;
			}
			
			Arena a = am.searchPlayer(p);
			if (a != null){
				p.sendMessage(mf.getMessage("already_ingame", true));
				return true;
			}
			
			Player challenger = p;
			OfflinePlayer receiver_op = Bukkit.getOfflinePlayer(args[0]);
			
			if (!receiver_op.isOnline()){
				p.sendMessage(mf.getMessage("player_offline", true));
				return true;
			}
			
			Player receiver = Bukkit.getPlayer(args[0]);
			
			if (am.searchPlayer(challenger) != null){
				p.sendMessage(mf.getMessage("player_ingame", true));
				return true;
			}
			
			if (!cp.containsKey(receiver)){
				cp.put(receiver, new ChallengePlayer(this , receiver));
			}
			
			try{
				if (!cp.get(challenger).accept(receiver)){
					p.sendMessage(mf.getMessage("no_request", true));
				}
			}catch(Exception ex){
				p.sendMessage(mf.getMessage("no_request", true));
			}

			return true;
		}
		
		else if(cmd.getName().equalsIgnoreCase("challenge")){
			
			if (args.length != 1){
				p.sendMessage(prefix+ " /challenge <Spieler>");
				return true;
			}
			
			Arena a = am.searchPlayer(p);
			if (a != null){
				p.sendMessage(mf.getMessage("already_ingame", true));
				return true;
			}
			
			Player challenger = p;
			OfflinePlayer receiver_op = Bukkit.getOfflinePlayer(args[0]);
			
			if (!receiver_op.isOnline()){
				p.sendMessage(mf.getMessage("player_offline", true));
				return true;
			}
			
			Player receiver = Bukkit.getPlayer(args[0]);
			
			if (am.searchPlayer(challenger) != null){
				p.sendMessage(mf.getMessage("player_ingame", true));
				return true;
			}
			
			if (!wait.contains(challenger)){
				if (!cp.containsKey(receiver)){
					cp.put(receiver, new ChallengePlayer(this , receiver));
				}
				
				cp.get(receiver).callHit(challenger);
				
				wait.add(challenger);
				
				this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						if (wait.contains(challenger)){
							wait.remove(challenger);
						}
					}
				} , 100L);
				
			}
			else{
				
				challenger.sendMessage(mf.getMessage("challenge_cooldown", true));
				
			}

			return true;
		}
		return true;
	}
	
}
