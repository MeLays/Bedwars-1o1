package de.melays.bw1o1;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.ChatColor;

public class PlaceHolderAPI extends EZPlaceholderHook {

    private main plugin;

    public PlaceHolderAPI(main ourPlugin) {
        super(ourPlugin, "bw1vs1");
        this.plugin = ourPlugin;
    }
    

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
    	
        if (p == null) {
            return "";
        }
        if (identifier.equals("points")) {
        	return ""+plugin.bedwars1o1.getKey(p.getUniqueId(), "points");
        }
        if (identifier.equals("rank")) {
        	return ""+plugin.ranker.getRank(p.getUniqueId());
        }

        return null;
    }
}
