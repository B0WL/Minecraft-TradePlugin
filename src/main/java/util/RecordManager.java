package util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import main.Trade;
import net.md_5.bungee.api.ChatColor;

public class RecordManager {
	static JavaPlugin plugin = Trade.instance;
	
	static final String title = "["+plugin.getDescription().getName()+"] - ";

	public static void record(String reason, String record) {
		if (!plugin.getConfig().getBoolean("debug"))
			if (reason.contains("debug"))
				return;

		Logger logeer = plugin.getLogger();
		String log = 
				String.format("- %s", reason) 
				+ "\r\n" + record;

		logeer.log(Level.INFO, log);
	}

	public static void record(String reason, String record, Player player, Float price) {
		if (!plugin.getConfig().getBoolean("debug"))
			if (reason.contains("debug"))
				return;

		Logger logeer = plugin.getLogger();
		String log = 
				String.format("- %s", reason) 
				+ "\r\n" + record
				+ "\r\n" + String.format("User: %s (%s)", player.getDisplayName(), player.getUniqueId())
				+ "\r\n" + String.format("Price: %s", price);
		
		logeer.log(Level.INFO, log);
	}

	public static void message(Player player, String reason, String message) {
		player.sendMessage(ChatColor.YELLOW+ title +reason);
		player.sendMessage(message);
	}
	
	public static void message(Player player, String reason, String itemString, Float price) {
		String name = "";
		ItemStack item = ItemSerializer.stringToItem(itemString);
		
		if(item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else
			name = item.getType().name();
		player.sendMessage(ChatColor.YELLOW+title +reason+" item.");
		
		player.sendMessage(ChatColor.GRAY+ itemString);
		player.sendMessage(String.format("%s \"%s\" at %s.",reason,name,price));
	}

}
