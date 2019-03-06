package listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import database.Database;
import intenrnal.AuctionRecorder;
import intenrnal.MenuInventory;
import intenrnal.MenuInventoryHolder;
import main.Trade;
import util.ItemSerializer;

public class InventoryListener implements Listener {

	public InventoryListener() {

	}

	ItemStack getMenuItem(Player player, int slot) {
		ItemStack MenuItem = player.getOpenInventory().getTopInventory().getItem(slot);
		return MenuItem;
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {

		if (e.getInventory().getHolder() != null)
			if (e.getInventory().getHolder() instanceof MenuInventoryHolder) {
				e.setCancelled(true);
				ItemStack clickedItem = e.getCurrentItem();

				if (clickedItem.getItemMeta() != null) {
					String title = e.getInventory().getTitle();
					String clickedItemName = clickedItem.getItemMeta().getDisplayName();
					Player player = (Player) e.getWhoClicked();

					if (title.equals("Auction")) {
						if (clickedItemName.equals(ChatColor.RED + "Exit")) {
							player.closeInventory();
						} else

						if (clickedItemName.equals(ChatColor.YELLOW + "Item Sell")) {
							MenuInventory.onAuctionSell(player, null, 0);
						}
					} ////////////////////// Main

					if (title.equals("Auction : Sell")) {
						if (e.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
							if (clickedItemName.equals(ChatColor.GRAY + "Back")) {
								MenuInventory.onAuctionMain(player);
							} else

							if (clickedItemName.equals(ChatColor.RED + "Exit")) {
								player.closeInventory();
							} else

							if (clickedItemName.equals(ChatColor.GREEN + "Register")) {
								Database db = Trade.instance.getRDatabase();
								ItemStack item = getMenuItem(player, MenuInventory.sellItemSlot);
								String itemNBT = ItemSerializer.itemToString(item);

								String price = getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta()
										.getDisplayName();

								if (db != null)
									if (player.getInventory().contains(item))
										if (db.registItem(player, itemNBT, price) == 1) {
											player.getInventory().removeItem(item);
											player.closeInventory();

											String message = itemNBT + "\r\n" + price;
											String reason = "Item Registered";
											AuctionRecorder.recordAuction(reason, message);
											AuctionRecorder.messageAuction(player, reason, message);
										}

							}

							if (e.getRawSlot() == MenuInventory.sellPriceSlot) {/// 가격설정버튼, 위치로 보고 찾는다.
								MenuInventory.onAuctionPrice(
										player, Integer.parseInt(getMenuItem(player, MenuInventory.sellPriceSlot)
												.getItemMeta().getDisplayName()),
										getMenuItem(player, MenuInventory.sellItemSlot));
							}
						}

						else {// 버튼이 아닌경우, 즉 아이템
							MenuInventory.onAuctionSell(player, clickedItem, Integer.parseInt(
									getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta().getDisplayName()));
						}
					} ////////////////////// Sell

					if (title.contains("Price")) {
						if (e.getClickedInventory() == player.getOpenInventory().getTopInventory()) {

							int price = Integer
									.parseInt(player.getOpenInventory().getTopInventory().getTitle().split(" ")[2]);

							if (clickedItemName.equals(ChatColor.GREEN + "Confirm")) {
								/// 확인 버튼
								MenuInventory.onAuctionSell(player, getMenuItem(player, MenuInventory.priceItemSlot),
										price);

							} else {// 확인버튼이 아니면 파싱
								String buttonParsedString[] = clickedItemName.split(" ");
								String updownString = buttonParsedString[0];
								String buttonPriceString = buttonParsedString[1];

								if (updownString.contains("UP")) {
									price += Integer.parseInt(buttonPriceString);
									MenuInventory.onAuctionPrice(player, price,
											getMenuItem(player, MenuInventory.priceItemSlot));
								} else {
									price -= Integer.parseInt(buttonPriceString);
									if (price < 0)
										price = 0;
									MenuInventory.onAuctionPrice(player, price,
											getMenuItem(player, MenuInventory.priceItemSlot));
								}

							}
						}
					} ////////////////////// Price
				}

			}

	}

}
