package de.melays.bw1o1;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class Color {
	
	int color;
	
	public Color (String c){
		color = fromString(c);
	}
	
	//1 Orange
	//2 Purple
	//3 Light Blue
	//4 Yellow
	//5 Light Green
	//6 Pink
	//7 Dark Blue
	//8 Red
	//9 Black
	//10 White
	
	public int fromString (String s){
		if (s.contains("Orange")){
			return 1;
		}
		if (s.contains("Lila")){
			return 2;
		}
		if (s.contains("Hellblau")){
			return 3;
		}
		if (s.contains("Gelb")){
			return 4;
		}
		if (s.contains("Hellgrün")){
			return 5;
		}
		if (s.contains("Pink")){
			return 6;
		}
		if (s.contains("Dunkelblau")){
			return 7;
		}
		if (s.contains("Rot")){
			return 8;
		}
		if (s.contains("Schwarz")){
			return 9;
		}
		if (s.contains("Weiß")){
			return 10;
		}
		
		return 0;
	}
	
	public ChatColor toChatColor (){
		
		if (color == 1){
			return ChatColor.GOLD;
		}
		else if (color == 2){
			return ChatColor.DARK_PURPLE;
		}
		else if (color == 3){
			return ChatColor.BLUE;
		}
		else if (color == 4){
			return ChatColor.YELLOW;
		}
		else if (color == 5){
			return ChatColor.GREEN;
		}
		else if (color == 6){
			return ChatColor.LIGHT_PURPLE;
		}
		else if (color == 7){
			return ChatColor.DARK_BLUE;
		}
		else if (color == 8){
			return ChatColor.RED;
		}
		else if (color == 9){
			return ChatColor.BLACK;
		}
		else if (color == 10){
			return ChatColor.WHITE;
		}
		
		return ChatColor.WHITE;
	}
	
	public String toString (){
		
		if (color == 1){
			return "Orange";
		}
		else if (color == 2){
			return "Lila";
		}
		else if (color == 3){
			return "Hellblau";
		}
		else if (color == 4){
			return "Gelb";
		}
		else if (color == 5){
			return "Hellgrün";
		}
		else if (color == 6){
			return "Pink";
		}
		else if (color == 7){
			return "Dunkelblau";
		}
		else if (color == 8){
			return "Rot";
		}
		else if (color == 9){
			return "Schwarz";
		}
		else if (color == 10){
			return "Weiß";
		}
		
		return "Unknown";
	}
	
	public byte toByte (){
		
		if (color == 1){
			return 1;
		}
		else if (color == 2){
			return 10;
		}
		else if (color == 3){
			return 3;
		}
		else if (color == 4){
			return 4;
		}
		else if (color == 5){
			return 5;
		}
		else if (color == 6){
			return 6;
		}
		else if (color == 7){
			return 11;
		}
		else if (color == 8){
			return 14;
		}
		else if (color == 9){
			return 15;
		}
		else if (color == 10){
			return 0;
		}
		
		return 0;
	}
	
	public ItemStack getItemStack (boolean choosen , Player p){
		ItemBuilder b = new ItemBuilder(Material.STAINED_CLAY).setDurability(toByte());
		
		boolean permission = false;
		if (color < 5){
			permission = true;
		}
		else if (color >= 5 && color < 9){
			if (color > 4 && p.hasPermission("bw.vipcolor.1")){
				permission = true;
			}
		}
		else{
			if (p.hasPermission("bw.vipcolor.2")){
				permission = true;
			}
		}
		
		if (!permission){
			b.setName(toChatColor() +""+ ChatColor.STRIKETHROUGH + this.toString());
			b.addLoreLine(ChatColor.RED + "Du darfst diese Farbe nicht verwenden!");
		}
		
		else{
			if (!choosen){
				b.setName(toChatColor() + this.toString());
				b.addLoreLine(ChatColor.GRAY + "Linksklicken um diese Farbe primär auszuwählen,");
				b.addLoreLine(ChatColor.GRAY + "rechtsklicken um diese Farbe sekundär auszuwählen!");
			}
			else{
				b.setName(toChatColor() +""+ChatColor.BOLD + this.toString());
				b.addLoreLine(ChatColor.GRAY + "Du hast diese Farbe "+toChatColor()+"ausgewählt"+ChatColor.GRAY+"!");
			}
		}
		
		return b.toItemStack();
	}
	
	public boolean hasPermission (Player p){
		
		boolean permission = false;
		if (color < 5){
			permission = true;
		}
		else if (color >= 5 && color < 9){
			if (color > 4 && p.hasPermission("bw.vipcolor.1")){
				permission = true;
			}
		}
		else{
			if (p.hasPermission("bw.vipcolor.2")){
				permission = true;
			}
		}
		return permission;
	}

}
