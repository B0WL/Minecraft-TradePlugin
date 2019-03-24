package intenrnal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import database.Database;
import database.Product;
import main.Trade;
import util.GUIManager;
import util.ItemSerializer;

public class MenuInventory {

	// FLAG MENU_MAIN
	public static int mainBuySlot = 11;
	public static int mainSellSlot = 13;
	public static int mainListSlot = 15;
	public static int mainManagerSlot = 16;
	public static int mainExitSlot = 26;

	public static void onAuctionMain(Player player) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Auction : Main");

		GUIManager.setButton(inventory, Material.DIAMOND_BLOCK, ChatColor.GREEN + "Item Buy", mainBuySlot);
		GUIManager.setButton(inventory, Material.GOLD_BLOCK, ChatColor.YELLOW + "Item Sell", mainSellSlot);
		GUIManager.setButton(inventory, Material.BOOK, ChatColor.BLUE + "Trade List", mainListSlot);

		if (player.hasPermission("auction.manager"))
			GUIManager.setButton(inventory, Material.ANVIL, ChatColor.WHITE + "Management", mainManagerSlot);

		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", mainExitSlot);

		player.openInventory(inventory);
	}

	// FLAG MENU_SELL

	public static int sellItemSlot = 11;
	public static int sellPriceSlot = 12;
	public static int sellRegistSlot = 15;
	public static int sellBackSlot = 18;
	public static int sellExitSlot = 26;

	public static void onAuctionSell(Player player, ItemStack item, int price) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Auction : Sell");

		ItemMeta meta = null;
		meta = new ItemStack(Material.STONE).getItemMeta();
		ItemStack selectedItem = new ItemStack(Material.CHEST);
		if (item != null) {
			selectedItem = item;
		} else {
			meta.setDisplayName(ChatColor.GRAY + "Item Select");
			selectedItem.setItemMeta(meta);
		}
		inventory.setItem(sellItemSlot, selectedItem);
		GUIManager.setButton(inventory, Material.GOLD_BLOCK, String.valueOf(price), sellPriceSlot);
		GUIManager.setButton(inventory, Material.BOOK, ChatColor.GREEN + "Register", sellRegistSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", sellBackSlot);
		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", sellExitSlot);
		player.openInventory(inventory);

	}

	// FLAG MENU_PRICE
	public static int priceItemSlot = 13;
	public static int priceConfirmSlot = 26;

	public static int priceDownSlot[] = {10,11,12};
	public static int priceUpSlot[] = {16,15,14};

	public static void onAuctionPrice(Player player, int price, ItemStack item) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Price : " + String.valueOf(price));

		GUIManager.setButton(inventory, Material.IRON_NUGGET, ChatColor.RED + "Down 1", priceDownSlot[0]);
		GUIManager.setButton(inventory, Material.IRON_INGOT, ChatColor.RED + "Down 10", priceDownSlot[1]);
		GUIManager.setButton(inventory, Material.IRON_BLOCK, ChatColor.RED + "Down 100", priceDownSlot[2]);

		inventory.setItem(priceItemSlot, item);

		GUIManager.setButton(inventory, Material.GOLD_BLOCK, ChatColor.GREEN + "UP 100", priceUpSlot[2]);
		GUIManager.setButton(inventory, Material.GOLD_INGOT, ChatColor.GREEN + "UP 10", priceUpSlot[1]);
		GUIManager.setButton(inventory, Material.GOLD_NUGGET, ChatColor.GREEN + "UP 1", priceUpSlot[0]);

		GUIManager.setButton(inventory, Material.SUNFLOWER, ChatColor.GREEN + "Confirm", priceConfirmSlot);

		player.openInventory(inventory);
	}

	// FLAG MENU_DROP
	public static int checkItemSlot = 11;
	public static int checkDropSlot = 15;
	public static int checkBackSlot = 18;

	public static void onAuctionCheckDrop(Player player, ItemStack item, String price, String id, boolean isDrop) {
		String name = "";

		if (item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else
			name = item.getType().name();

		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Drop : " + name);
		inventory.setItem(checkItemSlot, item);

		String isDropString = "";
		if (isDrop) {
			isDropString = "Drop";
		} else {
			isDropString = "Delete";
		}

		GUIManager.setButton(inventory, Material.TNT, ChatColor.RED + isDropString + " this item", checkDropSlot);

		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", checkBackSlot);
		player.openInventory(inventory);
	}

	// FLAG MENU_BUY
	public static int buyPageBackSlot = 48;
	public static int buyPageSlot = 49;
	public static int buyPageNextSlot = 50;
	public static int buyBackSlot = 45;
	public static int buyExitSlot = 53;

	public static void onAuctionBuy(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Buy");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page, player);
		itemList(productList, inventory, page, 0,DB);
		player.openInventory(inventory);
	}

	// FLAG MENU_LIST
	public static void onAuctionList(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : List");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemUser(player, page);
		itemList(productList, inventory, page, 1,DB);
		player.openInventory(inventory);

	}

	// FLAG MENU_MANAGER

	public static void onAuctionManager(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Manager");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page);
		itemList(productList, inventory, page, 2,DB);
		player.openInventory(inventory);
	}

	// FLAG MENU_FUNC_ITEMLIST
	static void itemList(List<Product> productList, Inventory inventory, int page, int status, Database DB) {
		int numb = 0;
		if (productList != null)
			if (!productList.isEmpty())
				for (Product product : productList) {
					int id = product.getId();
					int price = product.getPrice();
					
					java.util.Date creation_time = null;
					try {
						creation_time = Database.format.parse(product.getCreation_time());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					java.util.Date present_time = new java.util.Date();
					long diff = present_time.getTime() - creation_time.getTime();
					int period = Trade.instance.getConfig().getInt("Regist_period");

					String hour = String.valueOf(period - (int) (diff / 1000 / 60 / 60));

					ItemStack item = ItemSerializer.stringToItem(product.getItem());
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					String sold = "";
					if (product.getStatus() == 1) {
						sold = ChatColor.YELLOW + "Sold Out";
					}
					else if (product.getStatus() == 0) {
						if (Integer.parseInt(hour) < 0) {
							sold = ChatColor.RED + "Failed";
						} 
						else {
							sold = ChatColor.GREEN + "On Sale";
						}
					} else if (product.getStatus() == 2) {
						sold = ChatColor.DARK_RED + "Stop Sale";
					} else {
						sold = "error";
					}

					if (meta.getLore() != null)
						lore.addAll(meta.getLore());

					lore.add(ChatColor.GRAY + "-----------------------");
					lore.add(ChatColor.WHITE + "Remain Hour");
					lore.add(ChatColor.YELLOW + hour + "hour");
					lore.add(ChatColor.WHITE + "Price");
					lore.add(ChatColor.YELLOW + Integer.toString(price));

					if (status > 0) {
						lore.add(ChatColor.WHITE + "Status");
						lore.add(sold);
						if (status > 1) {
							String uuid = product.getUUID();
							String name = DB.getDisplayName(uuid);
							
							lore.add("owner");
							lore.add(name);

							lore.add(ChatColor.YELLOW + "[SHIFT + LEFT_CLICK]" + ChatColor.WHITE + "BAN TOGGLE");

							lore.add(ChatColor.YELLOW + "[RIGHT_CLICK]" + ChatColor.WHITE + "ITEM BUY");
							lore.add(ChatColor.YELLOW + "[SHIFT + RIGHT_CLICK]" + ChatColor.WHITE + "ITEM DROP");
						}
					}

					lore.add(ChatColor.BLACK + "Product ID");
					lore.add(ChatColor.BLACK + Integer.toString(id));

					ItemStack button = item;
					meta.setLore(lore);
					button.setItemMeta(meta);

					inventory.setItem(numb, button);

					numb++;
				}
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.RED + "Back Page", buyPageBackSlot);
		GUIManager.setButton(inventory, Material.HEART_OF_THE_SEA, Integer.toString(page), buyPageSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GREEN + "Next Page", buyPageNextSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", buyBackSlot);
		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", buyExitSlot);
	}

}// FLAG MENU________________________________________
