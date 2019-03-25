package listener;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import database.Database;
import intenrnal.MenuInventory;
import intenrnal.MenuInventoryHolder;
import main.Trade;
import net.milkbowl.vault.economy.Economy;
import util.AuctionRecorder;
import util.GUIManager;
import util.ItemSerializer;
import util.SoundManager;

public class InventoryListener implements Listener {
	Economy econ;
	Database db;

	public InventoryListener() {
		econ = Trade.instance.getEconomy();
		db = Trade.instance.getRDatabase();

	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent e) {
		if (e.getClickedInventory() != null)
			if (e.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof MenuInventoryHolder) {
				ItemStack currentItem = e.getCurrentItem();

				if (currentItem.getItemMeta() != null)
					if (currentItem.getItemMeta().getDisplayName() != null) {

						e.setCancelled(true);

						Player player = (Player) e.getWhoClicked();
						Inventory menu = player.getOpenInventory().getTopInventory();
						String title = menu.getTitle();
						int slot = e.getRawSlot();

						SoundManager.clickSound(player);

						// FLAG LISTEN_MAIN
						if (title.contains("Main"))
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								if (slot == MenuInventory.mainExitSlot) {
									player.closeInventory();
								} else if (slot == MenuInventory.mainSellSlot) {
									MenuInventory.onAuctionSell(player, null, 0);
								}

								else if (slot == MenuInventory.mainBuySlot) {
									MenuInventory.onAuctionBuy(player, 1);
								} else if (slot == MenuInventory.mainListSlot) {
									MenuInventory.onAuctionList(player, 1);
								} else if (slot == MenuInventory.mainManagerSlot) {
									MenuInventory.onAuctionManager(player, 1);
								}
							} ////////////////////// Main ///////////////////////////////////////////

						// FLAG LISTEN_SELL
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
									String itemString = ItemSerializer.itemToString(item);

									String price = GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta()
											.getDisplayName();

									String material = item.getType().name();

									if (db != null)
										if (player.getInventory().contains(item)) {
											
											if (db.getProductCount(player)<Trade.instance.getConfig().getInt("register_number")
													|| player.hasPermission("auction.manager")) {
												
												if (db.registItem(player, itemString, price, material) == 1) {
													player.getInventory().removeItem(item);
													MenuInventory.onAuctionList(player, 1);

													AuctionRecorder.recordAuction("Regist", itemString, player, price);
													AuctionRecorder.messageAuction(player, "registered", itemString, price);

													SoundManager.successSound(player);
												}else {
													AuctionRecorder.messageAuction(player, "Failed", "DB Regist Error.");
													SoundManager.failedSound(player);
													player.closeInventory();
												}
											}else {
												AuctionRecorder.messageAuction(player, "Failed", "Excess Registration Count.");
												SoundManager.failedSound(player);
												player.closeInventory();
											}
										}else {
											AuctionRecorder.messageAuction(player, "Failed", "Have not this Item.");
											SoundManager.failedSound(player);
											player.closeInventory();
										}

								}

								if (e.getRawSlot() == MenuInventory.sellPriceSlot) {/// 가격설정버튼, 위치로 보고 찾는다.
									MenuInventory.onAuctionPrice(player,
											Integer.parseInt(
													GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta().getDisplayName()),
											GUIManager.getMenuItem(player, MenuInventory.sellItemSlot));
								}
							}

							else {// 버튼이 아닌경우, 즉 아이템 올리기
								MenuInventory.onAuctionSell(player, currentItem, Integer.parseInt(
										GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta().getDisplayName()));
							}
						} ////////////////////// Sell ////////////////////////////////////////////////////

						// FLAG LISTEN_PRICE
						if (title.contains("Price")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								int price = Integer.parseInt(player.getOpenInventory().getTopInventory().getTitle().split(" ")[2]);

								if (slot == MenuInventory.priceConfirmSlot) {
									/// 확인 버튼
									MenuInventory.onAuctionSell(player, GUIManager.getMenuItem(player, MenuInventory.priceItemSlot),
											price);

								} else if (e.getRawSlot() != MenuInventory.priceItemSlot) {// 확인버튼, 아이템이 아니면 증감버튼

									if (slot == MenuInventory.priceUpSlot[0])
										price += 1;
									else if (slot == MenuInventory.priceUpSlot[1])
										price += 10;
									else if (slot == MenuInventory.priceUpSlot[2])
										price += 100;
									else if (slot == MenuInventory.priceDownSlot[0])
										price -= 1;
									else if (slot == MenuInventory.priceDownSlot[1])
										price -= 10;
									else if (slot == MenuInventory.priceDownSlot[2])
										price -= 100;

									if (price < 0)
										price = 0;

									MenuInventory.onAuctionPrice(player, price,
											GUIManager.getMenuItem(player, MenuInventory.priceItemSlot));

								}
							}
						} ////////////////////// Price ///////////////////////////////////////////////

						// FLAG LISTEN_BUY
						if (title.contains("Buy")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								String pageString = GUIManager.getMenuItem(player, MenuInventory.buyPageSlot).getItemMeta()
										.getDisplayName();
								int page = Integer.parseInt(pageString);

								if (!itemListButton(player, slot, page, title, menu.getItem(44))) {// 모든 버튼이 아닌경우 경매장아이템

									if (db != null) {
										List<String> lore = currentItem.getItemMeta().getLore();

										if (itemBuy(player, lore)) {
											MenuInventory.onAuctionBuy(player, page);
											SoundManager.successSound(player);
										} else {
											SoundManager.failedSound(player);
											AuctionRecorder.messageAuction(player, "Failed", "Have not enough money.");
											player.closeInventory();
										}
									}

								}

							}
						} ///////////////////////////// BUY //////////////////////////////////////

						// FLAG LISTEN_LIST
						if (title.contains("List")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								String pageString = GUIManager.getMenuItem(player, MenuInventory.buyPageSlot).getItemMeta()
										.getDisplayName();
								int page = Integer.parseInt(pageString);

								if (!itemListButton(player, slot, page, title, menu.getItem(44))) {// 모든 버튼이 아닌경우 경매장아이템

									List<String> lore = currentItem.getItemMeta().getLore();

									if (db != null) {
										String status = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Status") + 1));
										if (status.contains("Sold Out")) {
											this.itemSaleSuccess(player, lore);

											MenuInventory.onAuctionList(player, page);
											SoundManager.successSound(player);

										} else if (status.contains("Failed")) {
											this.itemRecall(player, lore);

											MenuInventory.onAuctionList(player, page);
											SoundManager.failedSound(player);
											AuctionRecorder.messageAuction(player, "Failed", "Time out.");
										} else if (status.contains("On Sale") || status.contains("Waiting")) {// 판매중인것 제거
											this.itemDropMenu(player, lore, currentItem);
										}
									}
								}
							}
						} ////////////////// List/////////////////////

						// FLAG LISTEN_DROP
						if (title.contains("Drop")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								if (slot == MenuInventory.checkBackSlot) {
									MenuInventory.onAuctionList(player, 1);
								} else if (slot == MenuInventory.checkDropSlot) {
									if (db != null) {
										List<String> lore = menu.getItem(MenuInventory.checkItemSlot).getItemMeta().getLore();
										this.itemDelete(player, lore);

										MenuInventory.onAuctionList(player, 1);
									}
								}
							}
						} ///////////// Drop///////////////////

						// FLAG LISTEN_MANAGER
						if (title.contains("Manager")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								String pageString = GUIManager.getMenuItem(player, MenuInventory.buyPageSlot).getItemMeta()
										.getDisplayName();
								int page = Integer.parseInt(pageString);

								if (!itemListButton(player, slot, page, title, menu.getItem(44))) {
									if (db != null) {
										List<String> lore = currentItem.getItemMeta().getLore();

										if (e.isShiftClick()) {
											if (e.isLeftClick()) {
												this.itemToggleBan(player, lore);
												MenuInventory.onAuctionManager(player, page);
											}

											if (e.isRightClick()) {
												this.itemDelete(player, lore);
												MenuInventory.onAuctionManager(player, page);
											}
										} else {
											if (e.isLeftClick()) {
											}
											if (e.isRightClick()) {
												if (this.itemBuy(player, lore))
													MenuInventory.onAuctionManager(player, page);
												else {
													player.closeInventory();
												}
											}
										}
									}
								}

							}
						} ////////////// Manager/////////////

					}
			}
	}// FLAG LISTEN_____________________________

	void itemToggleBan(Player player, List<String> lore) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		String item = db.selectItem(id);
		String status = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Status") + 1));

		if (item != null) {
			if (!status.contains("Stop Sale"))
				db.setStatus(id, 2);
			else {
				db.setStatus(id, 0);
			}
		}
	}

	void itemDelete(Player player, List<String> lore) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));

		String item = db.selectItem(id);
		if (item != null) {
			db.deleteItem(id);
			player.getInventory().addItem(ItemSerializer.stringToItem(item));
		}
	}

	void itemRecall(Player player, List<String> lore) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		String item = db.selectItem(id);

		db.deleteItem(id);
		player.getInventory().addItem(ItemSerializer.stringToItem(item));
	}

	void itemDropMenu(Player player, List<String> lore, ItemStack itemStack) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		String price = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Price") + 1));
		String item = db.selectItem(id);
		if (item != null) {
			MenuInventory.onAuctionCheckDrop(player, itemStack, price, id, false);
		} else {
			SoundManager.failedSound(player);
			AuctionRecorder.messageAuction(player, "Failed", "It does not exist.");
			player.closeInventory();
		}
	}

	void itemSaleSuccess(Player player, List<String> lore) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		String price = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Price") + 1));
		String item = db.selectItem(id);

		db.deleteItem(id);
		econ.depositPlayer(player, Double.parseDouble(price));

		AuctionRecorder.recordAuction("SELL", item, player, price);
		AuctionRecorder.messageAuction(player, "sold", item, price);
	}

	boolean itemBuy(Player player, List<String> lore) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		String price = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Price") + 1));
		String item = db.selectItem(id);

		if (econ.has(player, Double.parseDouble(price))) {
			if (db.setStatus(id, 1) == 1) {
				player.getInventory().addItem(ItemSerializer.stringToItem(item));
				econ.withdrawPlayer(player, Integer.parseInt(price));

				AuctionRecorder.recordAuction("BUY", item, player, price);
				AuctionRecorder.messageAuction(player, "bought", item, price);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	boolean itemListButton(Player player, int slot, int page, String title, ItemStack lastItem) {
		if (slot == MenuInventory.buyPageBackSlot || slot == MenuInventory.buyPageNextSlot) {
			if (slot == MenuInventory.buyPageBackSlot) {
				if (page != 1) {
					page--;
				}
			} else {
				if (lastItem != null)
					page++;
			}
			if (title.contains("List"))
				MenuInventory.onAuctionList(player, page);
			else if (title.contains("Manager"))
				MenuInventory.onAuctionManager(player, page);

		} else if (slot == MenuInventory.buyBackSlot) {
			MenuInventory.onAuctionMain(player);
		} else if (slot == MenuInventory.buyExitSlot) {
			player.closeInventory();
		} else if (slot != MenuInventory.buyPageSlot) {
			return false;
		}
		return true;
	}

}

// TODO _______________________________
