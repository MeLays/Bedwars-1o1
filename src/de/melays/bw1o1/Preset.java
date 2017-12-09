package de.melays.bw1o1;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class Preset {
	
	Location pos1;
	Location pos2;
	main plugin;
	
	public Preset (main p , Location l1 , Location l2){
		generatePositions(l1 , l2);
		plugin = p;
	}
	
	public PresetList generateGenerateList(){
		
		int xsize = (int) (pos2.getX() - pos1.getX()) +1;
		int ysize = (int) (pos2.getY() - pos1.getY()) +1;
		int zsize = (int) (pos2.getZ() - pos1.getZ()) +1;

		
		ArrayList<AdvancedMaterial> end = new ArrayList<AdvancedMaterial>();
		
		for (int i = 0 ; i < xsize ; i ++){
			for (int j = 0 ; j < ysize ; j ++){
				for (int m = 0 ; m < zsize ; m ++){
					
					Location loc = new Location (pos1.getWorld() , pos1.getX() + i , pos1.getY() + j , pos1.getZ() + m);
					if (!(loc.getBlock().getType() == Material.AIR)){
						end.add(new AdvancedMaterial(loc.getBlock().getType() , loc.getBlock().getData() , loc.getBlock().getState()));
					}
					else{
						end.add(new AdvancedMaterial(null , (byte) 0 , null));
					}
					
					
				}
			}
		}
		
		return new PresetList (plugin, end , xsize , ysize , zsize);
	}
	
	public void generatePositions (Location l1 , Location l2){
		
		//Pos1 needs smaller X and smaller Y
		
		double xpos1;
		double ypos1;
		double zpos1;
		double xpos2;
		double ypos2;
		double zpos2;
		
		
		if (l1.getX() <= l2.getX()){
			
			xpos1 = l1.getX();
			xpos2 = l2.getX();
			
		}
		else{
			
			xpos1 = l2.getX();
			xpos2 = l1.getX();
			
		}
		
		if (l1.getY() <= l2.getY()){
			
			ypos1 = l1.getY();
			ypos2 = l2.getY();
			
		}
		else{
			
			ypos1 = l2.getY();
			ypos2 = l1.getY();
			
		}
		
		if (l1.getZ() <= l2.getZ()){
			
			zpos1 = l1.getZ();
			zpos2 = l2.getZ();
			
		}
		else{
			
			zpos1 = l2.getZ();
			zpos2 = l1.getZ();
			
		}
		
		pos1 = new Location (l1.getWorld() , xpos1 , ypos1 , zpos1);
		pos2 = new Location (l2.getWorld() , xpos2 , ypos2 , zpos2);
		
		
	}
	
}
