package listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import database.Database;
import main.Trade;
import util.AuctionRecorder;

public class PlayerListener implements Listener {
	Database db;

	public PlayerListener() {
		db = Trade.instance.getRDatabase();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		db.registPlayer(player);

		int soldout = db.getSoldOut(player.getUniqueId().toString());
		if (soldout > 0)
			AuctionRecorder.messageAuction(player, "Notice", String.format("%d items sold.", soldout));
		else
			AuctionRecorder.messageAuction(player, "Notice", "No news.");

	}

}
