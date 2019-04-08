package listener;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import database.Database;
import main.Trade;
import menu.MenuHolder;
import menu.MenuInventory;
import menu.MenuInventoryHolder;
import net.milkbowl.vault.economy.Economy;
import util.RecordManager;
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
	public void onPlayerDragInventory(InventoryDragEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory topInventory = player.getOpenInventory().getTopInventory();
		InventoryHolder menuHolder = topInventory.getHolder();

		if (menuHolder instanceof MenuInventoryHolder) {// 플러그인 메뉴임
			Map<Integer, ItemStack> slots = e.getNewItems();
			int size = topInventory.getSize();

			Iterator<Integer> iterator = slots.keySet().iterator();

			while (iterator.hasNext()) {
				Integer key = iterator.next();
				if (key < size) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerclickedInventory(InventoryClickEvent e) {

		Inventory clickedInventory = e.getClickedInventory();

		if (clickedInventory != null) {

			Player player = (Player) e.getWhoClicked();
			Inventory topInventory = player.getOpenInventory().getTopInventory();
			InventoryHolder menuHolder = topInventory.getHolder();

			if (menuHolder instanceof MenuInventoryHolder) {// 플러그인 메뉴임

				if (clickedInventory == topInventory || (clickedInventory != topInventory && e.isShiftClick())) {// 메뉴를 클릭했다
					e.setCancelled(true);
				} // 일단 취소

				ItemStack clickedItem = e.getCurrentItem();

				if (clickedItem.getItemMeta() == null) {// 허공클릭
				} else if (clickedItem.getItemMeta().getDisplayName() != null) {// 이름있는 아이템클릭
					SoundManager.clickSound(player);

					InventoryHolder clickHolder = clickedInventory.getHolder();
					int slot = e.getRawSlot();

					MenuInventoryHolder myMenuHolder = (MenuInventoryHolder) menuHolder;

					// FLAG LISTEN_MAIN
					if (myMenuHolder.holderIs(MenuHolder.MAIN)) {
						if (slot == MenuInventory.mainExitSlot) {
							player.closeInventory();
						} else if (slot == MenuInventory.mainSellSlot) {
							MenuInventory.onSell(player);
						}

						else if (slot == MenuInventory.mainBuySlot) {
							MenuInventory.onBuy(player);
						} else if (slot == MenuInventory.mainListSlot) {
							MenuInventory.onList(player);
						} else if (slot == MenuInventory.mainManagerSlot) {
							MenuInventory.onManager(player);
						} else if (slot == MenuInventory.mainRecordSlot) {
							MenuInventory.onRecordList(player);
						} else if (slot == MenuInventory.mainPriceSlot) {
							MenuInventory.onMarketPrice(player);
						}

					} ////////////////////// Main ///////////////////////////////////////////

					// FLAG LISTEN_SELL
					else if (myMenuHolder.holderIs(MenuHolder.SELL)) {
						if (clickHolder instanceof MenuInventoryHolder) {
							if (slot == MenuInventory.sellBackSlot) {
								MenuInventory.onMain(player);
							} else

							if (slot == MenuInventory.sellExitSlot) {
								player.closeInventory();
							} else

							if (slot == MenuInventory.sellRegistSlot) {
								Database db = Trade.instance.getRDatabase();
								ItemStack item = topInventory.getItem(MenuInventory.sellItemSlot);
								String itemString = ItemSerializer.itemToString(item);

								String price = topInventory.getItem(MenuInventory.sellPriceSlot).getItemMeta().getDisplayName();
								Float pricef = Float.parseFloat(price);

								String material = item.getType().name();

								if (db != null)
									if (player.getInventory().contains(item)) {

										if (db.getProductCount(player) < Trade.instance.getConfig().getInt("register_number")
												|| player.hasPermission("auction.manager")) {

											if (db.registItem(player.getUniqueId().toString(), itemString, pricef, material, 0) == 1) {
												player.getInventory().removeItem(item);
												MenuInventory.onList(player);

												RecordManager.record("Regist", itemString, player, pricef);
												RecordManager.message(player, "registered", itemString, pricef);

												SoundManager.successSound(player);
											} else {
												RecordManager.message(player, "Failed", "DB Regist Error.");
												SoundManager.failedSound(player);
												player.closeInventory();
											}
										} else {
											RecordManager.message(player, "Failed", "Excess Registration Count.");
											SoundManager.failedSound(player);
											player.closeInventory();
										}
									} else {
										RecordManager.message(player, "Failed", "Have not this Item.");
										SoundManager.failedSound(player);
										player.closeInventory();
									}

							}

							if (e.getRawSlot() == MenuInventory.sellPriceSlot) {/// 가격설정버튼, 위치로 보고 찾는다.
								Float pricef = Float
										.valueOf(topInventory.getItem(MenuInventory.sellPriceSlot).getItemMeta().getDisplayName());
								BigDecimal priceD = BigDecimal.valueOf(pricef);

								MenuInventory.onPrice(player, priceD, topInventory.getItem(MenuInventory.sellItemSlot), BigDecimal.ONE);
							}
						}

						else {// 버튼이 아닌경우, 즉 아이템 올리기
							MenuInventory.onSell(player, clickedItem,
									new BigDecimal(topInventory.getItem(MenuInventory.sellPriceSlot).getItemMeta().getDisplayName()),
									clickedItem.getAmount());
						}
					} ////////////////////// Sell ////////////////////////////////////////////////////

					// FLAG LISTEN_PRICE
					else if (myMenuHolder.holderIs(MenuHolder.PRICE)) {
						BigDecimal priceD = myMenuHolder.getPrice();

						if (slot == MenuInventory.priceConfirmSlot) {
							/// 확인 버튼
							ItemStack item = topInventory.getItem(MenuInventory.priceItemSlot);
							MenuInventory.onSell(player, item, priceD, item.getAmount());

						} else if (e.getRawSlot() != MenuInventory.priceItemSlot) {// 확인버튼, 아이템이 아니면 증감버튼
							String unit = topInventory.getItem(MenuInventory.priceUpSlot[0]).getItemMeta().getDisplayName()
									.split(" ")[1];

							BigDecimal priceUnitD = new BigDecimal(unit);
							BigDecimal hundredD = BigDecimal.TEN.multiply(BigDecimal.TEN);

							if (slot == MenuInventory.priceUpSlot[0])
								priceD = priceD.add(priceUnitD);
							else if (slot == MenuInventory.priceUpSlot[1])
								priceD = priceD.add(priceUnitD.multiply(BigDecimal.TEN));
							else if (slot == MenuInventory.priceUpSlot[2])
								priceD = priceD.add(priceUnitD.multiply(hundredD));

							else if (slot == MenuInventory.priceDownSlot[0])
								priceD = priceD.subtract(priceUnitD);
							else if (slot == MenuInventory.priceDownSlot[1])
								priceD = priceD.subtract(priceUnitD.multiply(BigDecimal.TEN));
							else if (slot == MenuInventory.priceDownSlot[2])
								priceD = priceD.subtract(priceUnitD.multiply(hundredD));

							else if (slot == MenuInventory.priceUnitSlot[0]) {
								priceUnitD = priceUnitD.multiply(BigDecimal.TEN);
							} else if (slot == MenuInventory.priceUnitSlot[1]) {
								priceUnitD = priceUnitD.divide(BigDecimal.TEN);
							}

							if (priceD.compareTo(BigDecimal.ZERO) == -1)
								priceD = BigDecimal.ZERO;

							MenuInventory.onPrice(player, priceD, topInventory.getItem(MenuInventory.priceItemSlot), priceUnitD);

						}
					} ////////////////////// Price ///////////////////////////////////////////////

					// FLAG LISTEN_BUY
					else if (myMenuHolder.holderIs(MenuHolder.BUY)) {
						String pageString = topInventory.getItem(MenuInventory.listPageSlot).getItemMeta().getDisplayName();
						int page = Integer.parseInt(pageString);
						if (slot == MenuInventory.listFindSlot) {
							MenuInventory.onFind(player);
						} else if (!itemListButton(player, slot, page, myMenuHolder, topInventory.getItem(44))) {// 모든 버튼이 아닌경우
																																																			// 경매장아이템

							if (db != null) {

								List<String> lore = clickedItem.getItemMeta().getLore();
								this.itemDropMenu(player, lore, clickedItem, clickedItem.getAmount());

							}

						}

					} ///////////////////////////// BUY //////////////////////////////////////

					// FLAG LISTEN_LIST
					else if (myMenuHolder.holderIs(MenuHolder.LIST)) {
						String pageString = topInventory.getItem(MenuInventory.listPageSlot).getItemMeta().getDisplayName();
						int page = Integer.parseInt(pageString);

						if (!itemListButton(player, slot, page, myMenuHolder, topInventory.getItem(44))) {// 모든 버튼이 아닌경우 경매장아이템

							List<String> lore = clickedItem.getItemMeta().getLore();

							if (db != null) {
								String status = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Status") + 1));
								if (status.contains("Sold Out")) {
									this.itemSaleSuccess(player, lore, clickedItem.getAmount());

									MenuInventory.onList(player, page);
									SoundManager.successSound(player);

								} else if (status.contains("Failed")) {
									if (this.itemRecall(player, lore)) {
										MenuInventory.onList(player, page);
										RecordManager.message(player, "Failed", "Time out.");
									} else {
										SoundManager.failedSound(player);
										player.closeInventory();
									}
								} else if (status.contains("On Sale") || status.contains("Waiting")) {// 판매중인것 제거
									this.itemDropMenu(player, lore, clickedItem);
								}
							}
						}
					} ////////////////// List/////////////////////

					// FLAG LISTEN_DROP
					else if (myMenuHolder.holderIs(MenuHolder.DROP)) {
						if (slot == MenuInventory.checkBackSlot) {
							MenuInventory.onMain(player);
						} else if (slot == MenuInventory.checkDropSlot) {
							if (db != null) {
								List<String> lore = topInventory.getItem(MenuInventory.checkItemSlot).getItemMeta().getLore();
								if (this.itemDelete(player, lore)) {
									MenuInventory.onMain(player);
									SoundManager.successSound(player);
								} else {
									player.closeInventory();
									SoundManager.failedSound(player);
								}
							}
						}
					} ///////////// Drop///////////////////

					// FLAG LISTEN_CONFIRM
					else if (myMenuHolder.holderIs(MenuHolder.CONFIRM)) {
						if (slot == MenuInventory.checkBackSlot) {
							MenuInventory.onMain(player);
						} else if (slot == MenuInventory.checkDropSlot) {
							if (db != null) {
								ItemStack checkItem = topInventory.getItem(MenuInventory.checkItemSlot);
								List<String> lore = checkItem.getItemMeta().getLore();
								ItemStack ammountItem = topInventory.getItem(MenuInventory.checkAmountSlot);

								int amount = ammountItem.getAmount();

								if (itemBuy(player, lore, amount)) {
									MenuInventory.onMain(player);
									SoundManager.successSound(player);
								} else {
									SoundManager.failedSound(player);
									player.closeInventory();
								}

							}
						} else if (slot != MenuInventory.checkAmountSlot && slot != MenuInventory.checkItemSlot) {
							ItemStack confirmItem = topInventory.getItem(MenuInventory.checkItemSlot);
							List<String> lore = confirmItem.getItemMeta().getLore();

							int currentAmount = topInventory.getItem(MenuInventory.checkItemSlot).getAmount();
							int amount = topInventory.getItem(MenuInventory.checkAmountSlot).getAmount();
							if (slot == MenuInventory.checkUpSlot[0])
								amount += 1;
							else if (slot == MenuInventory.checkUpSlot[1])
								amount += 10;
							else if (slot == MenuInventory.checkDownSlot[0])
								amount -= 1;
							else if (slot == MenuInventory.checkDownSlot[1])
								amount -= 10;

							if (amount < 1)
								amount = 1;
							else if (amount > currentAmount)
								amount = currentAmount;

							this.itemDropMenu(player, lore, confirmItem, amount);
						}
					} ////// LISTEN_CONFIRM//////////

					// FLAG LISTEN_MANAGER
					else if (myMenuHolder.holderIs(MenuHolder.MANAGER)) {
						String pageString = topInventory.getItem(MenuInventory.listPageSlot).getItemMeta().getDisplayName();
						int page = Integer.parseInt(pageString);

						if (slot == MenuInventory.listFindSlot) {
							MenuInventory.onFindManager(player);

						} else if (!itemListButton(player, slot, page, myMenuHolder, topInventory.getItem(44))) {
							if (db != null) {
								List<String> lore = clickedItem.getItemMeta().getLore();

								if (e.isShiftClick()) {
									if (e.isLeftClick()) {
										this.itemToggleBan(player, lore);
										if (myMenuHolder.holderIs(MenuHolder.MANAGER_MAT))
											MenuInventory.onManager(player, page, clickedItem.getType().name());
										else {
											MenuInventory.onManager(player, page);
										}
									}

									if (e.isRightClick()) {
										if (this.itemDelete(player, lore))
											if (myMenuHolder.holderIs(MenuHolder.MANAGER_MAT))
												MenuInventory.onManager(player, page, clickedItem.getType().name());
											else
												MenuInventory.onManager(player, page);
										else
											player.closeInventory();
									}
								} else {
									if (e.isLeftClick()) {
										this.itemDropMenu(player, lore, clickedItem, clickedItem.getAmount());
									}
									if (e.isRightClick()) {

									}
								}
							}
						}

					} ////////////// LISTEN_MANAGER/////////////

					// FLAG LISTEN_FIND
					else if (myMenuHolder.holderIs(MenuHolder.FIND)) {
						String pageString = topInventory.getItem(MenuInventory.listPageSlot).getItemMeta().getDisplayName();
						int page = Integer.parseInt(pageString);

						if (!itemListButton(player, slot, page, myMenuHolder, topInventory.getItem(44))) {
							if (db != null) {
								String material = clickedItem.getType().name();
								if (myMenuHolder.holderIs(MenuHolder.MANAGER_FIND))
									MenuInventory.onManager(player, page, material);
								else
									MenuInventory.onBuy(player, page, material);

							}
						}

					} ////////////// LISTEN_FIND/////////////

					// FLAG LISTEN_INFO
					else if (myMenuHolder.holderIs(MenuHolder.INFO)) {
						if (slot == MenuInventory.infoBackSlot) {
							MenuInventory.onMain(player);
						} else if (slot == MenuInventory.infoExitSlot) {
							player.closeInventory();
						}
					} ////////////////////////////////

				}
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

	boolean itemDelete(Player player, List<String> lore) {
		if (player.getInventory().firstEmpty() != -1) {
			String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));

			String item = db.selectItem(id);
			if (item != null) {
				db.deleteItem(id);
				player.getInventory().addItem(ItemSerializer.stringToItem(item));
			}
			return true;
		} else {
			RecordManager.message(player, "Failed", "Inventory is full");
		}

		return false;
	}

	boolean itemRecall(Player player, List<String> lore) {
		if (player.getInventory().firstEmpty() != -1) {
			String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
			String item = db.selectItem(id);
			db.deleteItem(id);
			player.getInventory().addItem(ItemSerializer.stringToItem(item));

			return true;
		} else {
			RecordManager.message(player, "Failed", "Inventory is full");
		}
		return false;
	}

	void itemDropMenu(Player player, List<String> lore, ItemStack itemStack) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		String item = db.selectItem(id);
		if (item != null) {
			MenuInventory.onCheck(player, itemStack);
		} else {
			SoundManager.failedSound(player);
			RecordManager.message(player, "Failed", "It does not exist.");
			player.closeInventory();
		}
	}

	void itemDropMenu(Player player, List<String> lore, ItemStack itemStack, int count) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		String item = db.selectItem(id);
		Float price = db.getPrice(id);
		if (item != null) {
			MenuInventory.onCheck(player, itemStack, count, price);
		} else {
			SoundManager.failedSound(player);
			RecordManager.message(player, "Failed", "It does not exist.");
			player.closeInventory();
		}
	}

	void itemSaleSuccess(Player player, List<String> lore, int amount) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
		Float price = db.getPrice(id);
		String item = db.selectItem(id);

		db.deleteItem(id);
		econ.depositPlayer(player, price * amount);

		RecordManager.record("SELL", item, player, price * amount);
		RecordManager.message(player, "sold", item, price * amount);
	}

	boolean itemBuy(Player player, List<String> lore, int buyAmount) {
		String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));

		String item = db.selectItem(id);
		String sellerID = db.getSeller(id);

		Float pricef = db.getPrice(id);
		Float priceWholef = pricef * buyAmount;

		Player seller = Bukkit.getPlayer(UUID.fromString(sellerID));

		if (econ.has(player, priceWholef)) {
			if (player.getInventory().firstEmpty() != -1) {
				ItemStack itemStack = ItemSerializer.stringToItem(item);
				String material = itemStack.getType().name();
				int wholeAmount = itemStack.getAmount();

				if (wholeAmount == buyAmount) {
					if (db.setStatus(id, 1) == 1) {// 1개 구매, 혹은 전체구매
						player.getInventory().addItem(itemStack);

						db.registRecord(player, sellerID, item, pricef, material);
						RecordManager.record("BUY", item, player, priceWholef);
						RecordManager.message(player, "bought", item, priceWholef);

						if (seller != null) {
							RecordManager.message(seller, "sold", item, priceWholef);
							SoundManager.successSound(seller);
						}
					} else {
						RecordManager.message(player, "Failed", "DB Error");
						return false;
					}
				} else {
					if (db.deleteItem(id) == 1) {// 일부 구매시
						int remainAmount = wholeAmount - buyAmount;

						ItemStack buyItem = itemStack;
						buyItem.setAmount(buyAmount);
						String buyItemString = ItemSerializer.itemToString(buyItem);

						ItemStack remainItem = itemStack;
						remainItem.setAmount(remainAmount);
						String remainItemString = ItemSerializer.itemToString(remainItem);

						db.registItem(sellerID, remainItemString, pricef, material, 0);
						db.registItem(sellerID, buyItemString, pricef, material, 1);

						player.getInventory().addItem(buyItem);

						db.registRecord(player, sellerID, buyItemString, pricef, material);
						RecordManager.record("BUY", buyItemString, player, priceWholef);
						RecordManager.message(player, "bought", buyItemString, priceWholef);

						if (seller != null) {
							RecordManager.message(seller, "sold", buyItemString, priceWholef);
							SoundManager.successSound(seller);
						}

					} else {
						RecordManager.message(player, "Failed", "DB Error");
						return false;
					}

				}
				econ.withdrawPlayer(player, priceWholef);
				return true;
			} else {
				RecordManager.message(player, "Failed", "Inventory is full");
			}
		} else {
			RecordManager.message(player, "Failed", "Have not enough money.");
		}

		return false;

	}

	boolean itemListButton(Player player, int slot, int page, MenuInventoryHolder inventoryHolder, ItemStack lastItem) {
		if (slot == MenuInventory.listPageBackSlot || slot == MenuInventory.listPageNextSlot) {
			if (slot == MenuInventory.listPageBackSlot) {
				if (page != 1) {
					page--;
				}
			} else {
				if (lastItem != null)
					page++;
			}
			MenuHolder holder = inventoryHolder.getHolder();

			if (holder == MenuHolder.LIST)
				MenuInventory.onList(player, page);
			else if (holder == MenuHolder.MANAGER) {
				if (holder == MenuHolder.MANAGER_MAT) {
					String material = inventoryHolder.getMaterial();
					MenuInventory.onManager(player, page, material);
				} else {
					MenuInventory.onManager(player, page);
				}
			} else if (holder == MenuHolder.BUY) {
				if (holder == MenuHolder.BUY_MAT) {
					String material = inventoryHolder.getMaterial();
					MenuInventory.onBuy(player, page, material);
				} else {
					MenuInventory.onBuy(player, page);
				}
			}

		} else if (slot == MenuInventory.listBackSlot) {
			MenuInventory.onMain(player);
		} else if (slot == MenuInventory.listExitSlot) {
			player.closeInventory();
		} else if (slot != MenuInventory.listPageSlot) {
			return false;
		}
		return true;
	}

}

// TODO _______________________________
