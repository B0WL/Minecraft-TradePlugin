package listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import database.Database;
import main.Trade;
import util.RecordManager;

public class PlayerListener implements Listener {
	Database db;

	public PlayerListener() {
		db = Trade.instance.getRDatabase();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		int soldout = db.getSoldOut(player.getUniqueId().toString());
		if (soldout > 0)
			RecordManager.message(player, "Notice", String.format("%d items sold.", soldout));
		else
			RecordManager.message(player, "Notice", "No news.");
	}

}
