package util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {

	
	public static void failedSound(Player player) {
		player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 10 ,29);
	}
	
	public static void clickSound(Player player) {
		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 10, 29);
	}
	
	public static void successSound(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 29);
	}
}
