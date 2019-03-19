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
import intenrnal.AuctionRecorder;
import intenrnal.MenuInventory;
import intenrnal.MenuInventoryHolder;
import main.Trade;
import net.milkbowl.vault.economy.Economy;
import util.GUIManager;
import util.ItemSerializer;

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
								if (slot == MenuInventory.mainListSlot) {
									MenuInventory.onAuctionList(player, 1);
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

									String price = GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta()
											.getDisplayName();

									if (db != null)
										if (player.getInventory().contains(item))
											if (db.registItem(player, itemNBT, price) == 1) {
												player.getInventory().removeItem(item);
												MenuInventory.onAuctionList(player, 1);

												String message = itemNBT + "\r\n" + price;
												String reason = "Item Registered";
												AuctionRecorder.recordAuction(reason, message);
												AuctionRecorder.messageAuction(player, reason, message);
											}

								}

								if (e.getRawSlot() == MenuInventory.sellPriceSlot) {/// ���ݼ�����ư, ��ġ�� ���� ã�´�.
									MenuInventory.onAuctionPrice(player,
											Integer.parseInt(
													GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta().getDisplayName()),
											GUIManager.getMenuItem(player, MenuInventory.sellItemSlot));
								}
							}

							else {// ��ư�� �ƴѰ��, �� ������
								MenuInventory.onAuctionSell(player, e.getCurrentItem(), Integer.parseInt(
										GUIManager.getMenuItem(player, MenuInventory.sellPriceSlot).getItemMeta().getDisplayName()));
							}
						} ////////////////////// Sell ////////////////////////////////////////////////////

						if (title.contains("Price")) {
							if (e.getClickedInventory().getHolder() instanceof MenuInventoryHolder) {
								int price = Integer.parseInt(player.getOpenInventory().getTopInventory().getTitle().split(" ")[2]);

								if (slot == MenuInventory.priceConfirmSlot) {
									/// Ȯ�� ��ư
									MenuInventory.onAuctionSell(player, GUIManager.getMenuItem(player, MenuInventory.priceItemSlot),
											price);

								} else if (e.getRawSlot() != MenuInventory.priceItemSlot) {// Ȯ�ι�ư, �������� �ƴϸ� �Ľ�
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
								} else if (slot != MenuInventory.buyPageSlot) {// ��� ��ư�� �ƴѰ�� ����������

									if (e.isLeftClick()) {
										if (db != null) {
											List<String> lore = e.getCurrentItem().getItemMeta().getLore();
											String id = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.BLACK + "Product ID") + 1));
											String price = ChatColor.stripColor(lore.get(lore.indexOf(ChatColor.WHITE + "Price") + 1));
											String item = db.selectItem(id);
											if (item != null)
												if (econ.has(player, Double.parseDouble(price)))
													if (db.setSold(id,1) == 1) {// db���� �Ǹŵ� ���·� ����
														MenuInventory.onAuctionBuy(player, page);
														player.getInventory().addItem(ItemSerializer.stringToItem(item));
														econ.withdrawPlayer(player, Integer.parseInt(price));

														AuctionRecorder.recordAuction("BUY", item);
														AuctionRecorder.messageAuction(player, "BUY", item);
													}
										}
									}
								}

							}
						} ///////////////////////////// BUY //////////////////////////////////////
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
								} else if (slot != MenuInventory.buyPageSlot) {// ��� ��ư�� �ƴѰ�� ����������
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
											if (status.contains("Sold Out") || status.contains("Stop Sale")) {
												db.deleteItem(id);
												econ.depositPlayer(player, Double.parseDouble(price));

												MenuInventory.onAuctionList(player, page);
											} else if (status.contains("Failed")) {
												db.deleteItem(id);
												player.getInventory().addItem(ItemSerializer.stringToItem(item));

												MenuInventory.onAuctionList(player, page);
											} else if(status.contains("On Sale")){/// �Ǹ���
												MenuInventory.onAuctionCheckDrop(player, e.getCurrentItem(), price, id,false);
											}
										}
									}

								}
							}
						} ////////////////// List/////////////////////

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
											db.setSold(id, 2);
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
