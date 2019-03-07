package intenrnal;

import org.bukkit.entity.Player;

public class AuctionRecorder {

	public static void recordAuction(String reason, String record) {
		System.out.println("[Auction: " + reason + "]");
		System.out.println(record);
		System.out.println("[------------------------]");
	}

	public static void messageAuction(Player player, String reason, String message) {
		player.sendMessage("[Auction: " + reason + "]");
		player.sendMessage(message);
		player.sendMessage("[------------------------]");
	}

}
