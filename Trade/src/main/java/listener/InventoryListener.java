package listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import intenrnal.MenuInventory;

public class InventoryListener implements Listener {
	MenuInventory menuInventory = new MenuInventory();
	
	
	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {

		if (e.getInventory().getTitle().equals("Auction")) {
			if (e.getCurrentItem().getItemMeta() != null)
				if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
					String buttonName = e.getCurrentItem().getItemMeta().getDisplayName();

					if (buttonName.equals(ChatColor.RED + "Exit")) {
						e.getWhoClicked().closeInventory();
					}
					if (buttonName.equals(ChatColor.YELLOW + "Item Sell")) {
						menuInventory.onAuctionSell((Player)e.getWhoClicked());
					}
					e.setCancelled(true);
				}
		}
		
		
		
		if (e.getInventory().getTitle().equals("Auction : Sell")) {
			if (e.getCurrentItem().getItemMeta() != null)
				if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
					String buttonName = e.getCurrentItem().getItemMeta().getDisplayName();


					if (buttonName.equals(ChatColor.YELLOW+"Item Select")) {
						menuInventory.onItemSelect((Player)e.getWhoClicked());
					}
					
					
					if (buttonName.equals(ChatColor.GRAY + "Back")) {
						menuInventory.onAuctionMain((Player)e.getWhoClicked());
					}
					if (buttonName.equals(ChatColor.RED + "Exit")) {
						e.getWhoClicked().closeInventory();
					}					

					e.setCancelled(true);
				}
		}
		
		if (e.getInventory().getTitle().equals("Auction : Select")) {
			if (e.getCurrentItem().getItemMeta() != null)
				if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
					String buttonName = e.getCurrentItem().getItemMeta().getDisplayName();


					if (buttonName.equals(ChatColor.GREEN+"Confirm")) {
						menuInventory.onAuctionSell((Player)e.getWhoClicked());
					}
					if (buttonName.equals(ChatColor.RED + "Cancel")) {
						e.getWhoClicked().closeInventory();
					}					

					e.setCancelled(true);
				}
		}
	}
	
	
	

}
