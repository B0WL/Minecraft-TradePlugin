package listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import intenrnal.MenuInventory;
import intenrnal.MenuInventoryHolder;
import main.Trade;

public class InventoryListener implements Listener {
	private MenuInventory menuInventory;

	public InventoryListener() {
		menuInventory = Trade.menuInventory;
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {
		if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof MenuInventoryHolder) {

			e.setCancelled(true);
			ItemStack clickedItem = e.getCurrentItem();
			
			if (clickedItem.getItemMeta() != null) {
				String title = e.getInventory().getTitle();
				String clickedItemName = clickedItem.getItemMeta().getDisplayName();
				Player player = (Player)e.getWhoClicked();
				
				
				

				if (title.equals("Auction")) {
					if (clickedItemName.equals(ChatColor.RED + "Exit")) {
						player.closeInventory();
					}else
						
					if (clickedItemName.equals(ChatColor.YELLOW + "Item Sell")) {
						menuInventory.onAuctionSell(player, null,0);
					}
				}//////////////////////Main
				
				if (title.equals("Auction : Sell")) {
					if(e.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
						if (clickedItemName.equals(ChatColor.GRAY + "Back")) {
							menuInventory.onAuctionMain(player);
						}else
						
						if (clickedItemName.equals(ChatColor.RED + "Exit")) {
							player.closeInventory();
						}else
						
						if (e.getRawSlot() == 19) {
							menuInventory.onAuctionPrice(player
									,Integer.parseInt(
									player.getOpenInventory().getTopInventory()
									.getItem(19).getItemMeta().getDisplayName())
									,player.getOpenInventory().getTopInventory()
									.getItem(10)
									);
						}
					}
					
					else {// 버튼이 아닌경우, 즉 아이템
						menuInventory.onAuctionSell(player, clickedItem,
								Integer.parseInt(
								player.getOpenInventory().getTopInventory()
								.getItem(19).getItemMeta().getDisplayName())
								);
					}
				}//////////////////////Sell
				
				if(title.contains("Price")) {					
					if(e.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
						if(!clickedItemName.equals(ChatColor.GREEN+"Confirm")) {//확인버튼이 아니면 파싱
							String priceString = player.getOpenInventory().getTopInventory().getTitle().split(" ")[2];
							String buttonParsedString[] = clickedItemName.split(" ");
							
							String updownString = buttonParsedString[0];
							String buttonPriceString = buttonParsedString[1];

							player.sendMessage(priceString);
							player.sendMessage(updownString);
							player.sendMessage(buttonPriceString);

							int price = Integer.parseInt(priceString);
							
							if(updownString.contains("UP")) {
								 price += Integer.parseInt(buttonPriceString);
								menuInventory.onAuctionPrice(player								
										,price
										,player.getOpenInventory().getTopInventory()
										.getItem(13)
										);
							}
							else {
								price -= Integer.parseInt(buttonPriceString);
								if(price < 0)
									price =0;
								menuInventory.onAuctionPrice(player								
										,price
										,player.getOpenInventory().getTopInventory()
										.getItem(13)
										);
							}
							
						}
					}
				}//////////////////////Price
			}

		}

	}

}
