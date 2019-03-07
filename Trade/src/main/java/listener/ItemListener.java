package listener;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener{
	public HashMap<Player,Location> SelectMap = new HashMap<Player, Location>();
	
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		

		Player player  = event.getPlayer();
		Block block = event.getClickedBlock();		
		
		if(action.equals(Action.RIGHT_CLICK_BLOCK)) {

			
			if(player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
						
				player.sendMessage("You click on this block: "+ block.getType().toString() );
				Location blockLoc = block.getLocation();
				
				
				SelectMap.put(player, blockLoc);
			}
		}
		
	}
	

}
