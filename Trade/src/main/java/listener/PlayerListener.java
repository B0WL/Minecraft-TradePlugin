package listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import database.Database;
import main.Trade;

public class PlayerListener implements Listener {
	Database db;

	public PlayerListener() {
		db = Trade.instance.getRDatabase();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		db.registPlayer(player);
	}

}
