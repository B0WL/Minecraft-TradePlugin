package listener;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import database.Database;
import intenrnal.AuctionRecorder;
import intenrnal.MenuInventory;
import intenrnal.MenuInventoryHolder;
import main.Trade;
import net.milkbowl.vault.economy.Economy;
import util.GUIManager;
import util.ItemSerializer;

public class InventoryListener implements Listener {
	Economy econ;

	public InventoryListener() {
		econ = Trade.instance.getEconomy();

	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {
		if (e.getClickedInventory() != null)
			if (e.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof MenuInventoryHolder)
				if (e.getCurrentItem().getItemMeta() != null)
					if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {

						e.setCancelled(true);

						Player player = (Player) e.getWhoClicked();
						Inventory menu = player.getOpenInventory().getTopInventory();
						String title = menu.getTitle();
						int slot = e.getRawSlot();

						if (title.contains("Main"))
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								if (slot == MenuInventory.mainExitSlot) {
									player.closeInventory();
								} else

								if (slot == MenuInventory.mainSellSlot) {
									MenuInventory.onAuctionSell(player, null, 0);
								}

								if (slot == MenuInventory.mainBuySlot) {
									MenuInventory.onAuctionBuy(player, 1);
								}
							} ////////////////////// Main ///////////////////////////////////////////

						if (title.contains("Sell")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								if (slot == MenuInventory.sellBackSlot) {
									MenuInventory.onAuctionMain(player);
								} else

								if (slot == MenuInventory.sellExitSlot) {
									player.closeInventory();
								} else

								if (slot == MenuInventory.sellRegistSlot) {
									Database db = Trade.instance.getRDatabase();
									ItemStack item = GUIManager.getMenuItem(player, MenuInventory.sellItemSlot);
									String itemNBT = ItemSerializer.itemToString(item);

									String price = GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot)
											.getItemMeta().getDisplayName();

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
									MenuInventory.onAuctionPrice(player,
											Integer.parseInt(GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot)
													.getItemMeta().getDisplayName()),
											GUIManager.getMenuItem(player, MenuInventory.sellItemSlot));
								}
							}

							else {// 버튼이 아닌경우, 즉 아이템
								MenuInventory.onAuctionSell(player, e.getCurrentItem(),
										Integer.parseInt(GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot)
												.getItemMeta().getDisplayName()));
							}
						} ////////////////////// Sell ////////////////////////////////////////////////////

						if (title.contains("Price")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								int price = Integer
										.parseInt(player.getOpenInventory().getTopInventory().getTitle().split(" ")[2]);

								if (slot == MenuInventory.priceConfirmSlot) {
									/// 확인 버튼
									MenuInventory.onAuctionSell(player,
											GUIManager.getMenuItem(player, MenuInventory.priceItemSlot), price);

								} else if (e.getRawSlot() != MenuInventory.priceItemSlot) {// 확인버튼, 아이템이 아니면 파싱
									String buttonParsedString[] = e.getCurrentItem().getItemMeta().getDisplayName()
											.split(" ");
									String updownString = buttonParsedString[0];
									String buttonPriceString = buttonParsedString[1];

									if (updownString.contains("UP")) {
										price += Integer.parseInt(buttonPriceString);
										MenuInventory.onAuctionPrice(player, price,
												GUIManager.getMenuItem(player, MenuInventory.priceItemSlot));
									} else {
										price -= Integer.parseInt(buttonPriceString);
										if (price < 0)
											price = 0;
										MenuInventory.onAuctionPrice(player, price,
												GUIManager.getMenuItem(player, MenuInventory.priceItemSlot));
									}

								}
							}
						} ////////////////////// Price ///////////////////////////////////////////////

						if (title.contains("Buy")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {

								if (slot == MenuInventory.buyPageBackSlot || slot == MenuInventory.buyPageNextSlot) {
									String pageString = GUIManager.getMenuItem(player, MenuInventory.buyPageSlot)
											.getItemMeta().getDisplayName();
									int page = Integer.parseInt(pageString);

									if (slot == MenuInventory.buyPageBackSlot) {
										if (page != 1) {
											page--;
										}
									} else {
										if (menu.getItem(44) != null)
											page++;
									}

									MenuInventory.onAuctionBuy(player, page);
								} else if (slot == MenuInventory.buyBackSlot) {
									MenuInventory.onAuctionMain(player);
								} else if (slot == MenuInventory.buyExitSlot) {
									player.closeInventory();
								} else if (slot != MenuInventory.buyPageSlot) {// 모든 버튼이 아닌경우 경매장아이템

									Database db = Trade.instance.getRDatabase();
									if (db != null) {
										List<String> lore = e.getCurrentItem().getItemMeta().getLore();
										String id = lore.get(lore.indexOf("Product ID")+1);
										String price = lore.get(lore.indexOf("Price")+1);
										String item = db.selectItem(id);
										if (item != null)
											if (db.buyItem(id) == 1) {// db에서 판매됨 상태로 변경
												player.closeInventory();
												player.getInventory().addItem(ItemSerializer.stringToItem(item));
												econ.withdrawPlayer(player, Integer.parseInt(price));
												
												AuctionRecorder.recordAuction("BUY", item);
												AuctionRecorder.messageAuction(player, "BUY", item);
											}

									}

								}

							}
						}

					}

	}

}
