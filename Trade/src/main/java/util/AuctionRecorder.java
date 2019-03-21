package util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class AuctionRecorder {
	
	static String title[] = {"[Auction: " ,"]"};

	public static void recordAuction(String reason, String record) {
		System.out.println(title[0] +reason+title[1]);
		System.out.println(record);
	}

	public static void recordAuction(String reason, String record, Player player, String price) {
		System.out.println(title[0] +reason+title[1]);
		System.out.println(record);
		System.out.println(player.getDisplayName());
		System.out.println(player.getUniqueId());
		System.out.println(price);
		
	}

	public static void messageAuction(Player player, String reason, String message) {
		player.sendMessage(ChatColor.YELLOW+ title[0] +reason+title[1]);
		player.sendMessage(message);
	}
	
	public static void messageAuction(Player player, String reason, String itemString, String price) {
		String name = "";
		ItemStack item = ItemSerializer.stringToItem(itemString);
		
		if(item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else
			name = item.getType().name();
		player.sendMessage(ChatColor.YELLOW+title[0]+"Item " +reason+title[1]);
		
		player.sendMessage(ChatColor.GRAY+ itemString);
		player.sendMessage(String.format("Success %s \"%s\" at %s.",reason,name,price));
	}

}
