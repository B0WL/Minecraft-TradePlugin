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
			if (e.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof MenuInventoryHolder)
				if (e.getCurrentItem().getItemMeta() != null)
					if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {

						e.setCancelled(true);

						Player player = (Player) e.getWhoClicked();
						Inventory menu = player.getOpenInventory().getTopInventory();
						String title = menu.getTitle();
						int slot = e.getRawSlot();

						SoundManager.clickSound(player);

						// TODO Listen_Main
						if (title.contains("Main"))
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {								
								if (slot == MenuInventory.mainExitSlot) {
									player.closeInventory();
								} 
								else if (slot == MenuInventory.mainSellSlot) {
									MenuInventory.onAuctionSell(player, null, 0);
								}

								else if (slot == MenuInventory.mainBuySlot) {
									MenuInventory.onAuctionBuy(player, 1);
								}
								else if (slot == MenuInventory.mainListSlot) {
									MenuInventory.onAuctionList(player, 1);
								}
							} ////////////////////// Main ///////////////////////////////////////////

						// TODO Listen_Sell
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

									String price = GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta()
											.getDisplayName();

									if (db != null)
										if (player.getInventory().contains(item))
											if (db.registItem(player, itemNBT, price) == 1) {
												player.getInventory().removeItem(item);
												MenuInventory.onAuctionList(player, 1);

												AuctionRecorder.recordAuction("Regist", itemNBT,player,price);
												AuctionRecorder.messageAuction(player, "registered", itemNBT,price);
												
												SoundManager.successSound(player);
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
								MenuInventory.onAuctionSell(player, e.getCurrentItem(), Integer.parseInt(
										GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta().getDisplayName()));
							}
						} ////////////////////// Sell ////////////////////////////////////////////////////

						// TODO Listen_Price
						if (title.contains("Price")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								int price = Integer.parseInt(player.getOpenInventory().getTopInventory().getTitle().split(" ")[2]);

								if (slot == MenuInventory.priceConfirmSlot) {
									/// 확인 버튼
									MenuInventory.onAuctionSell(player, GUIManager.getMenuItem(player, MenuInventory.priceItemSlot),
											price);

								} else if (e.getRawSlot() != MenuInventory.priceItemSlot) {// 확인버튼, 아이템이 아니면 파싱
									String buttonParsedString[] = e.getCurrentItem().getItemMeta().getDisplayName().split(" ");
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

						// TODO Listen_Buy
						if (title.contains("Buy")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								String pageString = GUIManager.getMenuItem(player, MenuInventory.buyPageSlot).getItemMeta()
										.getDisplayName();
								int page = Integer.parseInt(pageString);

								if (slot == MenuInventory.buyPageBackSlot || slot == MenuInventory.buyPageNextSlot) {
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

									if (e.isLeftClick()) {
										if (db != null) {
											List<String> lore = e.getCurrentItem().getItemMeta().getLore();
											String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
											String price = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Price") + 1));
											String item = db.selectItem(id);
											if (item != null)
												if (econ.has(player, Double.parseDouble(price))) {
													if (db.setSold(id, 1) == 1) {
														MenuInventory.onAuctionBuy(player, page);
														
														player.getInventory().addItem(ItemSerializer.stringToItem(item));
														econ.withdrawPlayer(player, Integer.parseInt(price));

														AuctionRecorder.recordAuction("BUY", item,player,price);
														AuctionRecorder.messageAuction(player, "bought", item, price);
														SoundManager.successSound(player);
													}
												}else {
													SoundManager.failedSound(player);	
													AuctionRecorder.messageAuction(player, "Failed", "Have not enough money.");
													
												}
										}
									}
								}

							}
						} ///////////////////////////// BUY //////////////////////////////////////

						// TODO Listen_List
						if (title.contains("List")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {

								if (slot == MenuInventory.buyPageBackSlot || slot == MenuInventory.buyPageNextSlot) {
									String pageString = GUIManager.getMenuItem(player, MenuInventory.buyPageSlot).getItemMeta()
											.getDisplayName();
									int page = Integer.parseInt(pageString);

									if (slot == MenuInventory.buyPageBackSlot) {
										if (page != 1) {
											page--;
										}
									} else {
										if (menu.getItem(44) != null)
											page++;
									}

									MenuInventory.onAuctionList(player, page);
								} else if (slot == MenuInventory.buyBackSlot) {
									MenuInventory.onAuctionMain(player);
								} else if (slot == MenuInventory.buyExitSlot) {
									player.closeInventory();
								} else if (slot != MenuInventory.buyPageSlot) {// 모든 버튼이 아닌경우 경매장아이템
									String pageString = GUIManager.getMenuItem(player, MenuInventory.buyPageSlot).getItemMeta()
											.getDisplayName();
									int page = Integer.parseInt(pageString);

									if (db != null) {
										List<String> lore = e.getCurrentItem().getItemMeta().getLore();
										String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
										String price = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Price") + 1));
										String item = db.selectItem(id);

										String status = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Status") + 1));
										if (item != null) {
											if (status.contains("Sold Out")) {
												db.deleteItem(id);
												econ.depositPlayer(player, Double.parseDouble(price));

												MenuInventory.onAuctionList(player, page);

												AuctionRecorder.recordAuction("SELL", item,player,price);
												AuctionRecorder.messageAuction(player, "sold", item, price);
												SoundManager.successSound(player);
											} else if (status.contains("Failed")) {
												db.deleteItem(id);
												player.getInventory().addItem(ItemSerializer.stringToItem(item));

												MenuInventory.onAuctionList(player, page);
												
												SoundManager.failedSound(player);	
												AuctionRecorder.messageAuction(player, "Failed", "Time out.");
											} else if (status.contains("On Sale")) {//판매중인것 제거
												MenuInventory.onAuctionCheckDrop(player, e.getCurrentItem(), price, id, false);
											}
										}
									}

								}
							}
						} ////////////////// List/////////////////////

						// TODO Listen_Drop
						if (title.contains("Drop")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {

								if (slot == MenuInventory.checkBackSlot) {
									MenuInventory.onAuctionList(player, 1);
								} else if (slot == MenuInventory.checkDropSlot) {
									if (db != null) {
										List<String> lore = menu.getItem(MenuInventory.checkItemSlot).getItemMeta().getLore();
										String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));

										String item = db.selectItem(id);
										if (item != null) {
											db.deleteItem(id);
											player.getInventory().addItem(ItemSerializer.stringToItem(item));

											MenuInventory.onAuctionList(player, 1);
										}
									}
								}
							}
						} ///////////// Drop///////////////////

					}
	}
}
