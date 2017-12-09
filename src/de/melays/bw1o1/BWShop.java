package de.melays.bw1o1;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class BWShop implements Listener{
	
	main plugin;
	
	public BWShop(main p){
		plugin = p;
	}
	
	  public static void removeInventoryItems(PlayerInventory inv, Material type, int amount) {
	      for (ItemStack is : inv.getContents()) {
	          if (is != null && is.getType() == type) {
	              int newamount = is.getAmount() - amount;
	              if (newamount > 0) {
	                  is.setAmount(newamount);
	                  break;
	              } else {
	                  inv.remove(is);
	                  amount = -newamount;
	                  if (amount == 0) break;
	              }
	          }
	      }
	  }
	  
	  public int getAmount(Inventory inv , Material m){
		  
		  int count = 0;
		  
		  for (ItemStack s : inv.getContents()){
			  if (s != null){
				  if (s.getType() == m){
					  count += s.getAmount();
				  }
			  }
		  }
		  
		  return count;
	  }
	  
	  @EventHandler
	  public void buyItem (InventoryClickEvent e){
		  try{
			  if (e.getInventory().getName().equals(ChatColor.DARK_AQUA + "Bedwars Shop")){
				  ItemStack s = e.getCurrentItem();
				  
				  if (e.getSlot() >= 9 && e.getSlot() <= 17 && e.getClickedInventory().equals(e.getView().getTopInventory())){
					  
					  
					  if (s.getType() == Material.AIR || s.getType() == Material.STAINED_GLASS_PANE){
						  return;
					  }
					  int price;
					  int returnprice;
					  try{
						  price = Integer.parseInt(s.getItemMeta().getLore().get(0).split(" ")[0].replace(ChatColor.RED+"", "").replace(ChatColor.GRAY+"", "").replace(ChatColor.GOLD+"", ""));
					  }
					  catch (Exception ex){
						  return;
					  }
					  
					  int amount = s.getAmount();
					  boolean shift = false;
					  int pricesave = price;
					  
					  if (e.isShiftClick()){
						  price = price * (s.getMaxStackSize() / s.getAmount());
						  amount = s.getMaxStackSize();
						  shift = true;
					  }
					  
					  int specifyslot = -1;
					  
					  if (e.getClick() == ClickType.NUMBER_KEY){
						  specifyslot = e.getHotbarButton();
					  }
					  
					  Material remove = Material.BARRIER;
					  
					  if (s.getItemMeta().getLore().get(0).endsWith("Bronze")){
						  remove = Material.CLAY_BRICK;
					  }
					  else if (s.getItemMeta().getLore().get(0).endsWith("Eisen")) {
						  remove = Material.IRON_INGOT;
					  }
					  else if (s.getItemMeta().getLore().get(0).endsWith("Gold")) {
						  remove = Material.GOLD_INGOT;
					  }
					  
					  if (!(getAmount(e.getWhoClicked().getInventory() , remove) >= price)){
						  
						  if (shift){
							  amount = (getAmount(e.getWhoClicked().getInventory() , remove) / pricesave) * s.getAmount();
							  price =  pricesave * (amount / s.getAmount());
							  if (amount == 0){
								  price = 0;
							  }
						  }
						  else{
							  ((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.DIG_WOOD , 1, 1);
							  return;
						  }
						  
					  }
					  
					  s = s.clone();
					  if (amount != 0){
						  ((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ITEM_PICKUP , 1, 1);
						  removeInventoryItems(e.getWhoClicked().getInventory() , remove , price);
					  }
					  else{
						  ((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.DIG_WOOD , 1, 1);
					  }
					  s.setAmount(amount);
					  ItemMeta m = s.getItemMeta();
					  if (m != null){
						  m.setDisplayName(null);
						  m.setLore(null);
						  s.setItemMeta(m);
					  }
					  if (specifyslot == -1){
						  e.getWhoClicked().getInventory().addItem(s);
						  Bukkit.getServer().getPluginManager().callEvent(new ShopEvent((Player) e.getWhoClicked() , s , amount));
					  }
					  else{
						  ItemStack temp = null;
						  try{
							  temp = e.getWhoClicked().getInventory().getItem(specifyslot).clone();
						  }
						  catch (Exception ex){
							  
						  }
						  e.getWhoClicked().getInventory().setItem(specifyslot, s);
						  Bukkit.getServer().getPluginManager().callEvent(new ShopEvent((Player) e.getWhoClicked() , s , amount));
						  try{
							  e.getWhoClicked().getInventory().addItem(temp);
						  }
						  catch (Exception ex){
							  
						  }
					  }
					  
					  
				  }
				  else if (e.getClickedInventory().equals(e.getView().getTopInventory())){
					  ((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.CLICK , 1, 1);
				  }
			  }
		  }catch(Exception ex){}
		  
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
	  public ItemStack getMultiEnchantItem (Material m , int amount , byte b , String customname, String lore , boolean enchanted, Enchantment ench, int level){
			ItemStack stack = new ItemStack(m , amount , b);
			if (enchanted){	
				stack.addEnchantment(ench, level);

			}
			ItemMeta meta = stack.getItemMeta();
			ArrayList<String> list = new ArrayList<String>();
			list.add(lore);
			meta.setLore(list);
			meta.setDisplayName(customname);
			stack.setItemMeta(meta);
			return stack;
		}
	public ItemStack getCustomItem (Material m , int amount , byte b , String customname, String lore , boolean enchanted){
		ItemStack stack = new ItemStack(m , amount , b);
		if (enchanted){			
		}
		ItemMeta meta = stack.getItemMeta();
		ArrayList<String> list = new ArrayList<String>();
		list.add(lore);
		meta.setLore(list);
		meta.setDisplayName(customname);
		stack.setItemMeta(meta);
		return stack;
	}
	public ItemStack getEnchantedItem (Material m , int amount , byte b , String customname, String lore , boolean enchanted){
		ItemStack stack = new ItemStack(m , amount , b);
		if (enchanted){
			stack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
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
	public ItemStack getCustomArmor (Material m , int amount , byte b , Color Farbe, String customname, String lore , boolean enchanted){
		ItemStack stack = new ItemStack(m , amount , b);
		if (enchanted){
			stack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
		}
		LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
		Color ArmorColor = Farbe;
		meta.setColor(ArmorColor);
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
	
	public ItemStack getPotion (PotionType t, int strenght , String customname , String lore){
		
		ItemStack s = new Potion (t , strenght).toItemStack(1);
		ItemMeta m = s.getItemMeta();
		ArrayList<String> al = new ArrayList<String>();
		al.add(lore);
		m.setLore(al);
		m.setDisplayName(customname);
		s.setItemMeta(m);
		
		return s;
}
	
	@EventHandler
	public void Shop (PlayerInteractEvent e){
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (e.getClickedBlock().getType() == Material.BEACON){
				e.setCancelled(true);
				Inventory BwShop = Bukkit.createInventory(null, 18, ChatColor.DARK_AQUA + "Bedwars Shop");
				p.openInventory(BwShop);
			}
		}	
	}		
	
	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
	  Entity entity = event.getRightClicked();
	  if (!(entity instanceof Villager))
	      return;
	 
		event.setCancelled(true);
		Inventory BwShop = Bukkit.createInventory(null, 18, ChatColor.DARK_AQUA + "Bedwars Shop");
		event.getPlayer().openInventory(BwShop);
	}
	
	public ItemStack addEnchantment (ItemStack s , Enchantment e , int level){
		s.addUnsafeEnchantment(e, level);
		return s;
	}
		
	@EventHandler
	public void OpenShop (InventoryOpenEvent e){
		if (e.getInventory().getName().equals(ChatColor.DARK_AQUA + "Bedwars Shop")){
			Inventory BwShop = e.getInventory();
			BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
			BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
			BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
			BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
			BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
			BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
			BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
			BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
			BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
			BwShop.setItem(13, getCustomItem(Material.SLIME_BALL, 1, (byte) 0, ChatColor.AQUA + "Shop", "", false));
			fillPanes(BwShop);
			if (plugin.am.searchPlayer((Player)e.getPlayer()) == null){
				e.setCancelled(true);
			}
			else if (plugin.am.searchPlayer((Player)e.getPlayer()).specs.contains((Player)e.getPlayer())){
				e.setCancelled(true);
			}
		}
	}
			
	@EventHandler
	public void InvClick (InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		Inventory Inv = p.getInventory();
		if (e.getInventory().getName().equals(ChatColor.DARK_AQUA + "Bedwars Shop")){
			if (plugin.am.searchPlayer((Player)e.getWhoClicked()) == null){
				e.getWhoClicked().closeInventory();
			}
			if (e.getInventory().contains(e.getCurrentItem())){
			if (!(e.getCurrentItem() == null)){
			if (!(e.getCurrentItem().getType() == Material.AIR)){
			e.setCancelled(true);
			boolean shift = false;
		      if (e.isShiftClick()) {
		        shift = true;
		      }
			Inventory BwShop = e.getInventory();
			ItemStack DmgBow = getMultiEnchantItem(Material.BOW, 1, (byte) 0, ChatColor.DARK_PURPLE + "Bogen I", ChatColor.GOLD + "3 Gold", true, Enchantment.ARROW_INFINITE, 1);
			DmgBow.setDurability((short) 264);
			ItemStack DmgBow2 = getMultiEnchantItem(Material.BOW, 1, (byte) 0, ChatColor.DARK_PURPLE + "Bogen II", ChatColor.GOLD + "7 Gold", true, Enchantment.ARROW_INFINITE, 1);
			DmgBow2.setDurability((short) 264);
			ItemStack DmgBow3 = getMultiEnchantItem(Material.BOW, 1, (byte) 0, ChatColor.DARK_PURPLE + "Bogen III", ChatColor.GOLD + "11 Gold", true, Enchantment.ARROW_INFINITE, 1);
			DmgBow3.setDurability((short) 264);
			int Team = 0;
			if (p.getPlayerListName().equals(ChatColor.GOLD + p.getName())){
				Team = 1;
			}
			else if (p.getPlayerListName().equals(ChatColor.LIGHT_PURPLE + p.getName())){
				Team = 2;
			}
			else if (p.getPlayerListName().equals(ChatColor.AQUA + p.getName())){
				Team = 3;
			}
			else if (p.getPlayerListName().equals(ChatColor.YELLOW + p.getName())){
				Team = 4;
			}
			else if (p.getPlayerListName().equals(ChatColor.GREEN + p.getName())){
				Team = 5;
			}
			else if (p.getPlayerListName().equals(ChatColor.DARK_GRAY + p.getName())){
				Team = 7;
			}
			else if (p.getPlayerListName().equals(ChatColor.GRAY + p.getName())){
				Team = 8;
			}
			else if (p.getPlayerListName().equals(ChatColor.DARK_AQUA + p.getName())){
				Team = 9;
			}
			else if (p.getPlayerListName().equals(ChatColor.DARK_PURPLE + p.getName())){
				Team = 10;
			}
			else if (p.getPlayerListName().equals(ChatColor.BLUE + p.getName())){
				Team = 11;
			}
			else if (p.getPlayerListName().equals(ChatColor.DARK_GREEN + p.getName())){
				Team = 13;
			}
			else if (p.getPlayerListName().equals(ChatColor.RED + p.getName())){
				Team = 14;
			}
			else if (p.getPlayerListName().equals(ChatColor.BLACK + p.getName())){
				Team = 15;
			}
			Color ArmorColor = Color.WHITE;
			Color ArmorColorBlack = Color.BLACK;
			if (Team == 1){
				ArmorColor = Color.ORANGE;
			}
			if (Team == 2){
				ArmorColor = Color.FUCHSIA;
			}
			if (Team == 3){
				ArmorColor = Color.AQUA;
			}
			if (Team == 4){
				ArmorColor = Color.YELLOW;
			}
			if (Team == 5){
				ArmorColor = Color.LIME;
			}
			if (Team == 7){
				ArmorColor = Color.GRAY;
			}
			if (Team == 8){
				ArmorColor = Color.SILVER;
			}
			if (Team == 9){
				ArmorColor = Color.TEAL;
			}
			if (Team == 10){
				ArmorColor = Color.PURPLE;
			}
			if (Team == 11){
				ArmorColor = Color.BLUE;
			}
			if (Team == 13){
				ArmorColor = Color.GREEN;
			}
			if (Team == 14){
				ArmorColor = Color.RED;
			}
			if (Team == 15){
				ArmorColor = Color.BLACK;
			}
			
			if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Blöcke")){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", true));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				BwShop.setItem(9, new ItemStack(Material.AIR));
				BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem(11, getCustomItem(Material.RED_SANDSTONE, 2, (byte) 0, "Sandstein", ChatColor.RED + "1 Bronze", false));
				BwShop.setItem(12, getCustomItem(Material.ENDER_STONE, 1, (byte) 0, "Endstein", ChatColor.RED + "6 Bronze", false));
				BwShop.setItem(13, getCustomItem(Material.IRON_BLOCK, 1, (byte) 0, "Eisenblock", ChatColor.GRAY + "3 Eisen", false));
				BwShop.setItem(14, getCustomItem(Material.STAINED_GLASS, 1, (byte) Team, "Glas", ChatColor.RED + "3 Bronze", false));
				BwShop.setItem(15, getCustomItem(Material.GLOWSTONE, 4, (byte) 0, "Glowstone", ChatColor.RED + "14 Bronze", false));
				BwShop.setItem(16, new ItemStack(Material.AIR));
				BwShop.setItem(17, new ItemStack(Material.AIR));
			}
			else if (e.getSlot() == 1){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", true));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				
				BwShop.setItem(9, addEnchantment(getCustomArmor(Material.LEATHER_HELMET, 1, (byte) 0, ArmorColor, ChatColor.BLUE + "Lederhelm", ChatColor.RED + "1 Bronze", false), Enchantment.PROTECTION_ENVIRONMENTAL , 1));
				BwShop.setItem(10, addEnchantment(getCustomArmor(Material.LEATHER_LEGGINGS, 1, (byte) 0, ArmorColor, ChatColor.BLUE + "Lederhose", ChatColor.RED + "1 Bronze", false), Enchantment.PROTECTION_ENVIRONMENTAL , 1));
				BwShop.setItem(11, addEnchantment(getCustomArmor(Material.LEATHER_BOOTS, 1, (byte) 0, ArmorColor, ChatColor.BLUE + "Lederschuhe", ChatColor.RED + "1 Bronze", false), Enchantment.PROTECTION_ENVIRONMENTAL , 1));
				BwShop.setItem(12, new ItemStack(Material.AIR));
				BwShop.setItem(13, addEnchantment(getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.DARK_BLUE + "Brustplatte", ChatColor.GRAY + "1 Eisen", false), Enchantment.DURABILITY , 1));
				BwShop.setItem(14, addEnchantment(addEnchantment(getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.DARK_BLUE + "Brustplatte I", ChatColor.GRAY + "3 Eisen", false), Enchantment.PROTECTION_ENVIRONMENTAL , 1), Enchantment.DURABILITY , 1));
				BwShop.setItem(15, addEnchantment(addEnchantment(getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.DARK_BLUE + "Brustplatte II", ChatColor.GRAY + "7 Eisen", false), Enchantment.PROTECTION_ENVIRONMENTAL , 2), Enchantment.DURABILITY , 1));
				BwShop.setItem(16, addEnchantment(addEnchantment(addEnchantment(getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.DARK_BLUE + "Brustplatte III", ChatColor.GRAY + "11 Eisen", false), Enchantment.PROTECTION_ENVIRONMENTAL , 2), Enchantment.DURABILITY , 1), Enchantment.THORNS , 1));
				BwShop.setItem(17, getCustomArmor(Material.LEATHER_CHESTPLATE, 1, (byte) 0, ArmorColorBlack, ChatColor.DARK_BLUE + "Sprengweste", ChatColor.GOLD + "6 Gold", false));
			}
			else if (e.getSlot() == 2){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", true));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				
				BwShop.setItem(9, new ItemStack(Material.AIR));
				BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem(11, new ItemStack(Material.AIR));
				BwShop.setItem(12, getCustomItem(Material.WOOD_PICKAXE, 1, (byte) 0, ChatColor.YELLOW + "Spitzhacke I", ChatColor.RED + "7 Bronze", false));
				BwShop.setItem(13, getCustomItem(Material.STONE_PICKAXE, 1, (byte) 0, ChatColor.YELLOW + "Spitzhacke II", ChatColor.GRAY + "2 Eisen", false));
				BwShop.setItem(14, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.YELLOW + "Spitzhacke III", ChatColor.GOLD + "1 Gold", false));
				BwShop.setItem(15, new ItemStack(Material.AIR));
				BwShop.setItem(16, new ItemStack(Material.AIR));
				BwShop.setItem(17, new ItemStack(Material.AIR));
			}
			else if (e.getSlot() == 3){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", true));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				
				BwShop.setItem(9, new ItemStack(Material.AIR));
				BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem(11, addEnchantment(getCustomItem(Material.STICK, 1, (byte) 0, ChatColor.RED + "Knüppel", ChatColor.RED + "10 Bronze", false),Enchantment.KNOCKBACK , 1));
				BwShop.setItem(12, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.RED + "Holzschwert", ChatColor.GRAY + "1 Eisen", false));
				BwShop.setItem(13, addEnchantment(addEnchantment(getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.RED + "Holzschwert I", ChatColor.GRAY + "4 Eisen", false),Enchantment.DAMAGE_ALL , 1),Enchantment.DURABILITY , 1));
				BwShop.setItem(14, addEnchantment(addEnchantment(getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.RED + "Holzschwert II", ChatColor.GRAY + "6 Eisen", false),Enchantment.DAMAGE_ALL , 2),Enchantment.DURABILITY , 1));
				BwShop.setItem(15, addEnchantment(addEnchantment(getCustomItem(Material.IRON_SWORD, 1, (byte) 0, ChatColor.DARK_RED + "Eisenschwert", ChatColor.GOLD + "6 Gold", false),Enchantment.KNOCKBACK , 1),Enchantment.DAMAGE_ALL , 2));
				BwShop.setItem(16, new ItemStack(Material.AIR));
				BwShop.setItem(17, new ItemStack(Material.AIR));
			}
			else if (e.getSlot() == 4){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", true));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				
				BwShop.setItem(9, new ItemStack(Material.AIR));
				BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem(11, DmgBow);
				BwShop.setItem(12, DmgBow2);
				BwShop.setItem(13, DmgBow3);
				BwShop.setItem(14, new ItemStack(Material.AIR));
				BwShop.setItem(15, getCustomItem(Material.ARROW, 1, (byte) 0, ChatColor.LIGHT_PURPLE + "Pfeil", ChatColor.GOLD + "1 Gold", false));
				BwShop.setItem(16, new ItemStack(Material.AIR));
				BwShop.setItem(17, new ItemStack(Material.AIR));
			}
			else if (e.getSlot() == 5){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", true));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				
				BwShop.setItem(9, new ItemStack(Material.AIR));
				BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem(11, getCustomItem(Material.APPLE, 1, (byte) 0, ChatColor.DARK_GREEN + "Apfel", ChatColor.RED + "2 Bronze", false));
				BwShop.setItem(12, getCustomItem(Material.COOKED_BEEF, 1, (byte) 0, ChatColor.DARK_GREEN + "Fleisch", ChatColor.RED + "4 Bronze", false));
				BwShop.setItem(13, new ItemStack(Material.AIR));
				BwShop.setItem(14, new ItemStack(Material.AIR));
				BwShop.setItem(15, getCustomItem(Material.GOLDEN_APPLE, 1, (byte) 0, ChatColor.DARK_GREEN + "Goldapfel", ChatColor.GOLD + "2 Gold", false));
				BwShop.setItem(16, new ItemStack(Material.AIR));
				BwShop.setItem(17, new ItemStack(Material.AIR));
			}
			else if (e.getSlot() == 6){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", true));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				
				BwShop.setItem(9, new ItemStack(Material.AIR));
				BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem(11, new ItemStack(Material.AIR));
				BwShop.setItem(12, getCustomItem(Material.CHEST, 1, (byte) 0, ChatColor.GREEN + "Kiste", ChatColor.GRAY + "2 Eisen", false));
				BwShop.setItem(13, new ItemStack(Material.AIR));
				BwShop.setItem(14, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.GREEN + "Teamkiste", ChatColor.GOLD + "2 Gold", false));
				BwShop.setItem(15, new ItemStack(Material.AIR));
				BwShop.setItem(16, new ItemStack(Material.AIR));
				BwShop.setItem(17, new ItemStack(Material.AIR));
			}
			else if (e.getSlot() == 7){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", true));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", false));
				
				BwShop.setItem (9 , getPotion(PotionType.INSTANT_HEAL, 1, ChatColor.DARK_AQUA + "Heilung I" , ChatColor.GRAY + "5 Eisen"));
				BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem (11 , getPotion(PotionType.INSTANT_HEAL, 2, ChatColor.DARK_AQUA + "Heilung II" , ChatColor.GRAY + "8 Eisen"));
				BwShop.setItem(12, new ItemStack(Material.AIR));
				BwShop.setItem (13 , getPotion(PotionType.STRENGTH, 1, ChatColor.DARK_AQUA + "Stärke" , ChatColor.GOLD + "8 Gold"));
				BwShop.setItem(14, new ItemStack(Material.AIR));
				//BwShop.setItem (15 , getPotion(PotionType.INVISIBILITY, 1, ChatColor.DARK_AQUA + "Haste"));
				BwShop.setItem(16, new ItemStack(Material.AIR));
				BwShop.setItem(15, new ItemStack(Material.AIR));
				BwShop.setItem(17, new ItemStack(Material.AIR));
				//BwShop.setItem (17 , getPotion(PotionType.JUMP, 1, ChatColor.DARK_AQUA + "Sprungkraft"));
			}
			else if (e.getSlot() == 8){
				BwShop.setItem(0, getCustomItem(Material.RED_SANDSTONE, 1, (byte) 0, ChatColor.AQUA + "Blöcke", "", false));
				BwShop.setItem(1, getCustomItem(Material.CHAINMAIL_CHESTPLATE, 1, (byte) 0, ChatColor.AQUA + "Rüstung", "", false));
				BwShop.setItem(2, getCustomItem(Material.IRON_PICKAXE, 1, (byte) 0, ChatColor.AQUA + "Spitzhacken", "", false));
				BwShop.setItem(3, getCustomItem(Material.WOOD_SWORD, 1, (byte) 0, ChatColor.AQUA + "Schwerter", "", false));
				BwShop.setItem(4, getCustomItem(Material.BOW, 1, (byte) 0, ChatColor.AQUA + "Bögen", "", false));
				BwShop.setItem(5, getCustomItem(Material.CAKE, 1, (byte) 0, ChatColor.AQUA + "Nahrung", "", false));
				BwShop.setItem(6, getCustomItem(Material.ENDER_CHEST, 1, (byte) 0, ChatColor.AQUA + "Kisten", "", false));
				BwShop.setItem(7, getCustomItem(Material.POTION, 1, (byte) 0, ChatColor.AQUA + "Tränke", "", false));
				BwShop.setItem(8, getCustomItem(Material.EMERALD, 1, (byte) 0, ChatColor.AQUA + "Spezial", "", true));
				
				BwShop.setItem(9, getCustomItem(Material.LADDER, 1, (byte) 0, ChatColor.GOLD + "Leiter", ChatColor.RED + "2 Bronze", false));
				//BwShop.setItem(10, new ItemStack(Material.AIR));
				BwShop.setItem(11, new ItemStack(Material.AIR));
				//BwShop.setItem(12, new ItemStack(Material.AIR));
				BwShop.setItem(10, getCustomItem(Material.FIREWORK, 1, (byte) 0, ChatColor.GOLD + "Teleporter", ChatColor.GRAY + "10 Eisen", false));
				//BwShop.setItem(11, getCustomItem(Material.ARMOR_STAND, 1, (byte) 0, ChatColor.GOLD + "Mobiler Shop", ChatColor.GRAY + "12 Eisen", false));
				BwShop.setItem(12, getCustomItem(Material.TNT, 1, (byte) 0, ChatColor.GOLD + "TNT", ChatColor.GOLD + "3 Gold", false));
				BwShop.setItem(13, new ItemStack(Material.AIR));
				BwShop.setItem(14, new ItemStack(Material.AIR));
				//BwShop.setItem(15, new ItemStack(Material.AIR));
				//BwShop.setItem(16, new ItemStack(Material.AIR));
				//BwShop.setItem(14, getCustomItem(Material.MONSTER_EGG, 1, (byte) 0, ChatColor.GOLD + "Fallschirm", ChatColor.GOLD + "2 Gold", false));
				BwShop.setItem(15, getCustomItem(Material.NETHER_STAR, 1, (byte) 0, ChatColor.GOLD + "Rettungsplattform", ChatColor.GOLD + "3 Gold", false));
				BwShop.setItem(16, getCustomItem(Material.ENDER_PEARL, 1, (byte) 0, ChatColor.GOLD + "Enderperle", ChatColor.GOLD + "14 Gold", false));
				BwShop.setItem(17, getCustomItem(Material.WEB, 1, (byte) 0, ChatColor.GOLD + "Spinnenweben", ChatColor.RED + "20 Bronze", false));
			}
			fillPanes(e.getInventory());

			
				}
			}
		    }
		}
	}

}//End-Semicolon