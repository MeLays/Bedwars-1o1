package de.melays.bw1o1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ArenaMeta {
	
	HashMap<String , Location> meta = new HashMap<String , Location> ();
	
	public ArenaMeta(){
	}
	
	public void addData (String s , Location l){
		meta.put(s+"_"+UUID.randomUUID(), l);
	}
	
	public ArrayList<Location> getMeta (String s){
		ArrayList<Location> ls = new ArrayList<Location>();
		for (String str : meta.keySet()){
			if (str.startsWith(s)){
				ls.add(meta.get(str));
			}
		}
		return ls;
	}
	
	public ArrayList<Location> getTeamData (String s , Player p , Arena a){
		Location loc = null;
		Location loc_rel = null;
		if (p == a.p1){
			loc = a.l1;
			loc_rel = a.l2;
		}
		else{
			loc = a.l2;
			loc_rel = a.l1;
		}
		ArrayList<Location> ls = new ArrayList<Location>();
		for (String str : meta.keySet()){
			if (str.startsWith(s)){
				
				double d_corr = meta.get(str).distanceSquared(loc);
				double d_wrong = meta.get(str).distanceSquared(loc_rel);
				
				if (d_corr < d_wrong){
					ls.add(meta.get(str));
				}
				
			}
		}
		return ls;
	}
	
}
