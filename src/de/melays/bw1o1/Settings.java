package de.melays.bw1o1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;

public class Settings {
	
	int bronze = 10; //TICKS
	int gold = 60; //SECONDS
	int iron = 10; //SECONDS
	
	boolean cobwebtime = true;
	boolean cobwebtimebed = true;
	int cobtime = 10; //SECONDS
	
	boolean spawnprotection = true;
	
	Color primary = new Color ("Orange");
	Color sekundary = new Color ("Lila");
	
	int time = 20;
	
	Player p;
	main plugin;
	
	public Settings (Player pl , main m){
		this.p = pl;
		this.plugin = m;
		loadSettings();
		if (primary.color == 0){
			primary.color = 1;
		}
		if (sekundary.color == 0){
			sekundary.color = 2;
		}
	}
	
	public void loadSettings (){
		if (plugin.BW1o1Settings.getKey(p.getUniqueId(), "bronze") != 0){
			bronze = plugin.BW1o1Settings.getKey(p.getUniqueId(), "bronze");
			gold = plugin.BW1o1Settings.getKey(p.getUniqueId(), "gold");
			iron = plugin.BW1o1Settings.getKey(p.getUniqueId(), "iron");
			cobtime = plugin.BW1o1Settings.getKey(p.getUniqueId(), "cobtime");
			time = plugin.BW1o1Settings.getKey(p.getUniqueId(), "time");
			primary.color = plugin.BW1o1Settings.getKey(p.getUniqueId(), "color1");
			sekundary.color = plugin.BW1o1Settings.getKey(p.getUniqueId(), "color2");
			cobwebtime = Boolean.parseBoolean(plugin.BW1o1Settings.getStringKey(p.getUniqueId(), "cobwebtime"));
			cobwebtimebed = Boolean.parseBoolean(plugin.BW1o1Settings.getStringKey(p.getUniqueId(), "cobwebtimebed"));
			spawnprotection = Boolean.parseBoolean(plugin.BW1o1Settings.getStringKey(p.getUniqueId(), "spawnprotection"));
		}
	}
	
	public void saveSettings (){
		plugin.BW1o1Settings.setKey(p.getUniqueId(), "bronze" , bronze);
		plugin.BW1o1Settings.setKey(p.getUniqueId(), "gold" , gold);
		plugin.BW1o1Settings.setKey(p.getUniqueId(), "iron" , iron);
		plugin.BW1o1Settings.setKey(p.getUniqueId(), "cobtime" , cobtime);
		plugin.BW1o1Settings.setKey(p.getUniqueId(), "time" , time);
		plugin.BW1o1Settings.setKey(p.getUniqueId(), "color1" , primary.color);
		plugin.BW1o1Settings.setKey(p.getUniqueId(), "color2" , sekundary.color);
		plugin.BW1o1Settings.setStringKey(p.getUniqueId(), "cobwebtime", Boolean.toString(cobwebtime));
		plugin.BW1o1Settings.setStringKey(p.getUniqueId(), "cobwebtimebed", Boolean.toString(cobwebtimebed));
		plugin.BW1o1Settings.setStringKey(p.getUniqueId(), "spawnprotection", Boolean.toString(spawnprotection));
	}
	
	public void openInventory(){
		
		Inventory inv = Bukkit.createInventory(null, 6*9 , ChatColor.GRAY + "Einstellungen");
		updateInv(inv);
		p.openInventory(inv);

	}
	
	public void openColorInventory(){
		
		Inventory inv = Bukkit.createInventory(null, 3*9 , ChatColor.GRAY + "Farbauswahl");
		updateColorInv(inv);
		p.openInventory(inv);

	}
	
	public String booleanToGer (boolean b){
		if (b){
			return ChatColor.AQUA + "Ja";
		}
		return ChatColor.RED + "Nein";
	}
	
	public void sendMessage (Player p){
		p.sendMessage(plugin.prefix + " Die Einstellungen von "+ChatColor.AQUA +"" + ChatColor.BOLD+this.p.getName()+ChatColor.GRAY+":");
		p.sendMessage(plugin.prefix + " Bronze Spawnrate: " + ChatColor.AQUA+bronze + ChatColor.GRAY + " (Ticks)");
		p.sendMessage(plugin.prefix + " Eisen Spawnrate: " + ChatColor.AQUA+iron + ChatColor.GRAY + " (Sekunden)");
		p.sendMessage(plugin.prefix + " Gold Spawnrate: " + ChatColor.AQUA+gold + ChatColor.GRAY + " (Sekunden)");
		p.sendMessage(plugin.prefix + " max. Rundenlänge: " + ChatColor.AQUA+time + ChatColor.GRAY + " (Minuten)");
		p.sendMessage(plugin.prefix + " Cobwebs verschwinden nach: " + ChatColor.AQUA+cobtime + ChatColor.GRAY + " (Sekunden)");
		p.sendMessage(plugin.prefix + " Cobwebs verschwinden: " + booleanToGer(cobwebtime));
		p.sendMessage(plugin.prefix + " Cobwebs am Bett: " + booleanToGer(cobwebtimebed));
		p.sendMessage(plugin.prefix + " Spawnprotection: " + booleanToGer(spawnprotection));
	}
	
	public void updateInv (Inventory inv){
		
		inv.clear();
		
		//Buttons +
		inv.setItem(0, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.GREEN + "+").toItemStack());
		inv.setItem(2, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.GREEN + "+").toItemStack());
		inv.setItem(4, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.GREEN + "+").toItemStack());
		inv.setItem(6, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.GREEN + "+").toItemStack());
		inv.setItem(8, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.GREEN + "+").toItemStack());
		
		//Buttons -
		inv.setItem(18, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.RED + "-").toItemStack());
		inv.setItem(20, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.RED + "-").toItemStack());
		inv.setItem(22, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.RED + "-").toItemStack());
		inv.setItem(24, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.RED + "-").toItemStack());
		inv.setItem(26, new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.RED + "-").toItemStack());
		
		//Icons
		inv.setItem(9, new ItemBuilder(Material.CLAY_BRICK , bronze).setName(ChatColor.AQUA + "Bronze").addLoreLine(ChatColor.GRAY+"in Ticks").toItemStack());
		inv.setItem(11, new ItemBuilder(Material.IRON_INGOT , iron).setName(ChatColor.AQUA + "Eisen").addLoreLine(ChatColor.GRAY+"in Sekunden").toItemStack());
		inv.setItem(13, new ItemBuilder(Material.GOLD_INGOT , gold).setName(ChatColor.AQUA + "Gold").addLoreLine(ChatColor.GRAY+"in Sekunden").toItemStack());
		inv.setItem(15, new ItemBuilder(Material.WEB , cobtime).setName(ChatColor.AQUA + "Web-Despawn").addLoreLine(ChatColor.GRAY+"in Sekunden").toItemStack());
		inv.setItem(17, new ItemBuilder(Material.WATCH , time).setName(ChatColor.AQUA + "Spielzeit").addLoreLine(ChatColor.GRAY+"in Minuten").toItemStack());
		inv.setItem(37, new ItemBuilder(Material.WEB , 1).setName(ChatColor.AQUA + "Web-Despawn").toItemStack());
		inv.setItem(40, new ItemBuilder(Material.IRON_CHESTPLATE , 1).setName(ChatColor.AQUA + "Spawnprotection").toItemStack());
		inv.setItem(43, new ItemBuilder(Material.WEB , 1).setName(ChatColor.AQUA + "Web-Despawn").addLoreLine(ChatColor.GRAY+"am Bett").toItemStack());
		
		//Toggle
		inv.setItem(46, new ItemBuilder(Material.INK_SACK , 1).setDurability((short) 1).setName(ChatColor.RED + "Deaktiviert").toItemStack());
		if (cobwebtime){
			inv.setItem(46, new ItemBuilder(Material.INK_SACK , 1).setDurability((short) 10).setName(ChatColor.GREEN + "Aktiviert").toItemStack());
		}
		inv.setItem(49, new ItemBuilder(Material.INK_SACK , 1).setDurability((short) 1).setName(ChatColor.RED + "Deaktiviert").toItemStack());
		if (spawnprotection){
			inv.setItem(49, new ItemBuilder(Material.INK_SACK , 1).setDurability((short) 10).setName(ChatColor.GREEN + "Aktiviert").toItemStack());
		}
		inv.setItem(52, new ItemBuilder(Material.INK_SACK , 1).setDurability((short) 10).setName(ChatColor.GREEN + "Aktiviert").toItemStack());
		if (cobwebtimebed){
			inv.setItem(52, new ItemBuilder(Material.INK_SACK , 1).setDurability((short) 1).setName(ChatColor.RED + "Deaktiviert").toItemStack());
		}
		
		
	}
	
	public void updateColorInv (Inventory inv){
		inv.clear();
		
		inv.setItem(0, new ItemBuilder (Material.STAINED_GLASS_PANE).setName(ChatColor.GRAY + "Normale Farben").setDurability((byte)5).toItemStack());
		inv.setItem(1, new Color("Orange").getItemStack((primary.toString().equals("Orange") || sekundary.toString().equals("Orange")), p));
		inv.setItem(2, new Color("Lila").getItemStack((primary.toString().equals("Lila") || sekundary.toString().equals("Lila")), p));
		inv.setItem(3, new Color("Hellblau").getItemStack((primary.toString().equals("Hellblau") || sekundary.toString().equals("Hellblau")), p));
		inv.setItem(4, new Color("Gelb").getItemStack((primary.toString().equals("Gelb") || sekundary.toString().equals("Gelb")), p));
		
		inv.setItem(9, new ItemBuilder (Material.STAINED_GLASS_PANE).setName(ChatColor.YELLOW + "Premium Farben").setDurability((byte)4).toItemStack());
		inv.setItem(10, new Color("Hellgrün").getItemStack((primary.toString().equals("Hellgrün") || sekundary.toString().equals("Hellgrün")), p));
		inv.setItem(11, new Color("Pink").getItemStack((primary.toString().equals("Pink") || sekundary.toString().equals("Pink")), p));
		inv.setItem(12, new Color("Dunkelblau").getItemStack((primary.toString().equals("Dunkelblau") || sekundary.toString().equals("Dunkelblau")), p));
		inv.setItem(13, new Color("Rot").getItemStack((primary.toString().equals("Rot") || sekundary.toString().equals("Rot")), p));
		
		inv.setItem(18, new ItemBuilder (Material.STAINED_GLASS_PANE).setName(ChatColor.GOLD + "Elite Farben").setDurability((byte)1).toItemStack());
		inv.setItem(20, new Color("Schwarz").getItemStack((primary.toString().equals("Schwarz") || sekundary.toString().equals("Schwarz")), p));
		inv.setItem(21, new Color("Weiß").getItemStack((primary.toString().equals("Weiß") || sekundary.toString().equals("Weiß")), p));
		
		inv.setItem(15, new ItemBuilder(Material.STAINED_CLAY).setDurability(primary.toByte()).setName(primary.toChatColor()+"Primäre Farbe").toItemStack());
		inv.setItem(16, new ItemBuilder(Material.STAINED_CLAY).setDurability(sekundary.toByte()).setName(sekundary.toChatColor()+"Sekundäre Farbe").toItemStack());
	}
	
	public void callClick (InventoryClickEvent e){
		if (e.getClickedInventory().getName().equals(ChatColor.GRAY + "Einstellungen")){
			//Bronze +
			if (e.getSlot() == 0){
				if (bronze == 5)
					bronze = 10;
				else if (bronze == 10)
					bronze = 20;
				else if (bronze == 20)
					bronze = 40;
			}
			//Iron +
			if (e.getSlot() == 2){
				if (iron == 1)
					iron = 5;
				else if (iron == 5)
					iron = 10;
				else if (iron == 10)
					iron = 20;
				else if (iron == 20)
					iron = 40;
			}
			//Iron +
			if (e.getSlot() == 4){
				if (gold == 10)
					gold = 20;
				else if (gold == 20)
					gold = 40;
				else if (gold == 40)
					gold = 60;
			}
			//Web +
			if (e.getSlot() == 6){
				if (cobtime == 1)
					cobtime = 5;
				else if (cobtime == 5)
					cobtime = 10;
				else if (cobtime == 10)
					cobtime = 15;
				else if (cobtime == 15)
					cobtime = 20;
				else if (cobtime == 20)
					cobtime = 30;
			}
			//Lenght +
			if (e.getSlot() == 8){
				if (time == 5)
					time = 10;
				else if (time == 10)
					time = 15;
				else if (time == 15)
					time = 20;
				else if (time == 20)
					time = 30;
				else if (time == 30)
					time = 40;
			}
			
			//Bronze -
			if (e.getSlot() == 18){
				if (bronze == 40)
					bronze = 20;
				else if (bronze == 20)
					bronze = 10;
				else if (bronze == 10)
					bronze = 5;
			}
			//Iron +
			if (e.getSlot() == 20){
				if (iron == 40)
					iron = 20;
				else if (iron == 20)
					iron = 10;
				else if (iron == 10)
					iron = 5;
				else if (iron == 5)
					iron = 1;
			}
			//Iron +
			if (e.getSlot() == 22){
				if (gold == 60)
					gold = 40;
				else if (gold == 40)
					gold = 20;
				else if (gold == 20)
					gold = 10;
			}
			//Web +
			if (e.getSlot() == 24){
				
				if (cobtime == 30)
					cobtime = 20;
				else if (cobtime == 20)
					cobtime = 15;
				else if (cobtime == 15)
					cobtime = 10;
				else if (cobtime == 10)
					cobtime = 5;
				else if (cobtime == 5)
					cobtime = 1;
			}
			//Lenght +
			if (e.getSlot() == 26){
				
				if (time == 40)
					time = 30;
				else if (time == 30)
					time = 20;
				else if (time == 20)
					time = 15;
				else if (time == 15)
					time = 10;
				else if (time == 10)
					time = 5;
				
			}
			
			if (e.getSlot() == 46){
				if (cobwebtime){
					cobwebtime = false;
				}
				else{
					cobwebtime = true;
				}
			}
			
			if (e.getSlot() == 49){
				if (spawnprotection){
					spawnprotection = false;
				}
				else{
					spawnprotection = true;
				}
			}
			
			if (e.getSlot() == 52){
				if (cobwebtimebed){
					cobwebtimebed = false;
				}
				else{
					cobwebtimebed = true;
				}
			}
			
			updateInv(e.getClickedInventory());
		}
		else if (e.getClickedInventory().getName().equals(ChatColor.GRAY + "Farbauswahl")){
			if ((e.getSlot() >= 1 && e.getSlot() <= 4) || (e.getSlot() >= 10 && e.getSlot() <= 13) || (e.getSlot() >= 20 && e.getSlot() <= 21)){
				Color selected = new Color(e.getCurrentItem().getItemMeta().getDisplayName());
				if (selected.hasPermission((Player)e.getWhoClicked())){
					if (e.isLeftClick()){
						primary = selected;
						if (sekundary.color == primary.color){
							sekundary = new Color ("Lila");
							if (sekundary.color == primary.color){
								sekundary = new Color ("Orange");
							}
						}
					}
					else if (e.isRightClick()){
						sekundary = selected;
						if (sekundary.color == primary.color){
							primary = new Color ("Orange");
							if (sekundary.color == primary.color){
								primary = new Color ("Lila");
							}
						}
					}
				}
			}
			updateColorInv(e.getClickedInventory());
			p.getInventory().setItem(6, new ItemBuilder(Material.STAINED_CLAY).setDurability(plugin.settings.get(p.getUniqueId()).primary.toByte()).setName(ChatColor.GRAY+"Farbauswahl").toItemStack());
		}
	}
	
	
}
