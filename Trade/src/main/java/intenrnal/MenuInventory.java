package intenrnal;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import util.AuctionRecorder;
import util.GUIManager;
import util.ItemSerializer;

public class MenuInventory {

	// FLAG MENU_MAIN
	public static int mainReadmeSlot = 0;
	public static int mainManagerSlot = 1;
	public static int mainBuySlot = 11;
	public static int mainSellSlot = 13;
	public static int mainListSlot = 15;
	public static int mainExitSlot = 26;

	public static void onAuctionMain(Player player) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Auction : Main");
		ItemMeta meta = null;
		ItemStack readMe = new ItemStack(Material.PAPER);
		meta = readMe.getItemMeta();
		meta.setDisplayName("READ ME");//TODO READ ME Àû±â
		
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + "[RIGHT_CLICK]" + ChatColor.WHITE + " ITEM TRADING INFO");		
		meta.setLore(lore);
		
		readMe.setItemMeta(meta);
		inventory.setItem(mainReadmeSlot, readMe);

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

	public static void onAuctionSell(Player player, ItemStack item, BigDecimal price , int amount) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Auction : Sell");

		ItemMeta meta = null;
		ItemStack selectedItem = new ItemStack(Material.CHEST);
		meta = selectedItem.getItemMeta();
		if (item != null) {
			selectedItem = item;
		} else {
			meta.setDisplayName(ChatColor.GRAY + "Item Select");
			selectedItem.setItemMeta(meta);
		}
		inventory.setItem(sellItemSlot, selectedItem);
		
		ItemStack priceItem = new ItemStack(Material.GOLD_BLOCK);
		meta = priceItem.getItemMeta();
		meta.setDisplayName(String.valueOf(price));
		List<String> lore = new ArrayList<String>();
		lore.add(price.multiply(BigDecimal.valueOf(amount)).toString());
		lore.add(price+" X "+amount);
		meta.setLore(lore);
		priceItem.setItemMeta(meta);
		inventory.setItem(sellPriceSlot, priceItem);

		GUIManager.setButton(inventory, Material.BOOK, ChatColor.GREEN + "Register", sellRegistSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", sellBackSlot);
		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", sellExitSlot);
		player.openInventory(inventory);

	}

	// FLAG MENU_PRICE
	public static int priceItemSlot = 13;
	public static int priceConfirmSlot = 26;
	
	public static int priceUnitSlot[] = {4,22};

	public static int priceDownSlot[] = { 10, 11, 12 };
	public static int priceUpSlot[] = { 16, 15, 14 };

	public static void onAuctionPrice(Player player, BigDecimal price, ItemStack item, BigDecimal priceUnit) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Price : " + String.valueOf(price));
		BigDecimal hundredD = BigDecimal.TEN.multiply(BigDecimal.TEN);

		GUIManager.setButton(inventory, Material.IRON_NUGGET, ChatColor.RED + 
				"Down " +  (priceUnit).toString(), priceDownSlot[0]);
		GUIManager.setButton(inventory, Material.IRON_INGOT, ChatColor.RED + 
				"Down "+ (priceUnit.multiply(BigDecimal.TEN)).toString(), priceDownSlot[1]);
		GUIManager.setButton(inventory, Material.IRON_BLOCK, ChatColor.RED + 
				"Down "+(priceUnit.multiply(hundredD)).toString(), priceDownSlot[2]);

		GUIManager.setButton(inventory, Material.GHAST_TEAR, ChatColor.GREEN+"Unit Up", priceUnitSlot[0]);
		
		inventory.setItem(priceItemSlot, item);
		
		GUIManager.setButton(inventory, Material.GOLD_NUGGET, ChatColor.RED+"Unit Down", priceUnitSlot[1]);

		GUIManager.setButton(inventory, Material.GOLD_BLOCK, ChatColor.GREEN + 
				"UP "+(priceUnit.multiply(hundredD)).toString(), priceUpSlot[2]);
		GUIManager.setButton(inventory, Material.GOLD_INGOT, ChatColor.GREEN + 
				"UP "+(priceUnit.multiply(BigDecimal.TEN)).toString(), priceUpSlot[1]);
		GUIManager.setButton(inventory, Material.GOLD_NUGGET, ChatColor.GREEN + 
				"UP "+(priceUnit).toString(), priceUpSlot[0]);

		GUIManager.setButton(inventory, Material.SUNFLOWER, ChatColor.GREEN + "Confirm", priceConfirmSlot);

		player.openInventory(inventory);
	}

	// FLAG MENU_DROP
	public static int checkItemSlot = 10;

	public static int checkDownSlot[] = { 12, 13 };
	public static int checkAmountSlot = 14;
	public static int checkUpSlot[] = { 16, 15 };
	
	public static int checkDropSlot = 26;
	public static int checkBackSlot = 18;

	public static void onAuctionCheckDrop(Player player, ItemStack item) {
		String statusTitle = "";

		Material confirm = null;

		statusTitle = "Drop";
		confirm = Material.TNT;

		String name = "";

		if (item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else
			name = item.getType().name();

		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, statusTitle + " : " + name);
		inventory.setItem(checkItemSlot, item);

		GUIManager.setButton(inventory, confirm, ChatColor.RED + statusTitle + " this item", checkDropSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", checkBackSlot);
		player.openInventory(inventory);
	}

	public static void onAuctionCheckDrop(Player player, ItemStack item, int amount, Float price) {
		String statusTitle = "";

		Material confirm = null;
		statusTitle = "Confirm";
		confirm = Material.SUNFLOWER;

		String name = "";

		if (item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else
			name = item.getType().name();

		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, 
				statusTitle + " : " + name + " ["+(price*amount)+"]");
		
		inventory.setItem(checkItemSlot, item);

		GUIManager.setButton(inventory, Material.IRON_NUGGET, ChatColor.RED + "Down 1", checkDownSlot[0]);
		GUIManager.setButton(inventory, Material.IRON_INGOT, ChatColor.RED + "Down 10", checkDownSlot[1]);

		ItemMeta meta = null;
		ItemStack amountItem = new ItemStack(Material.CHEST);
		meta = amountItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Amount");
		amountItem.setItemMeta(meta);
		amountItem.setAmount(amount);
		inventory.setItem(checkAmountSlot, amountItem);

		GUIManager.setButton(inventory, Material.GOLD_INGOT, ChatColor.GREEN + "UP 10", checkUpSlot[1]);
		GUIManager.setButton(inventory, Material.GOLD_NUGGET, ChatColor.GREEN + "UP 1", checkUpSlot[0]);

		GUIManager.setButton(inventory, confirm, ChatColor.RED + statusTitle + " this item", checkDropSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", checkBackSlot);
		player.openInventory(inventory);
	}

	// FLAG MENU_BUY
	public static int buyBackSlot = 45;
	public static int buyPageBackSlot = 48;
	public static int buyPageSlot = 49;
	public static int buyPageNextSlot = 50;
	public static int buyFindSlot = 51;
	public static int buyExitSlot = 53;
	public static void onAuctionBuy(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Buy");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page, player);
		
		itemList(productList, inventory, page, 0, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", buyFindSlot);
		player.openInventory(inventory);
	}
	
	//FLAG MENU_BUY_MAT
	public static void onAuctionBuy(Player player, int page, String material) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Buy - "+material);
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page, player,material);
		
		itemList(productList, inventory, page, 0, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", buyFindSlot);
		player.openInventory(inventory);
	}
	
	// FLAG MENU_LIST
	public static void onAuctionList(Player player, int page) {
		Database DB = Trade.instance.getRDatabase();
		int regiNumb = Trade.instance.getConfig().getInt("register_number");
		int haveNumb = DB.getProductCount(player);

		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54,
				String.format("Auction : List [%d/%d]", haveNumb, regiNumb));

		List<Product> productList = DB.listItemUser(player, page);
		itemList(productList, inventory, page, 1, DB);
		player.openInventory(inventory);

	}
	// FLAG MENU_MANAGER
	public static void onAuctionManager(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Manager");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page);
		itemList(productList, inventory, page, 2, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", buyFindSlot);
		
		player.openInventory(inventory);
	}
	// FLAG MENU_MANAGER_MAT
	public static void onAuctionManager(Player player, int page, String material) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Manager - "+material);
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page, material);
		itemList(productList, inventory, page, 2, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", buyFindSlot);
		
		player.openInventory(inventory);
	}
	
	// FLAG MENU_FIND
	public static void onAuctionFind(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Find");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemGroupMaterial(page, player);
		itemList(productList, inventory, page, -1, DB);
		player.openInventory(inventory);
	}	
	// FLAG MENU_FIND_MANAGER
	public static void onAuctionFindManager(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Find - ALL");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemGroupMaterial(page);
		itemList(productList, inventory, page, -1, DB);
		player.openInventory(inventory);
	}

	
	
	
	
	//FLAG MENU_ITEM_INFO
	public static int infoMaterialSlot = 11;
	public static int infoPriceSlot = 15;
	public static int infoBackSlot = 18;
	public static int infoExitSlot = 26;
	
	
	public static void onAuctionItemInfo(Player player, Material material) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Auction : Info - "+material);
		
		GUIManager.setButton(inventory, material, material.name(), infoMaterialSlot);

		ItemMeta meta = null;
		ItemStack selectedItem = new ItemStack(material);
		meta = selectedItem.getItemMeta();
		
		ItemStack priceItem = new ItemStack(Material.BOOK);
		meta = priceItem.getItemMeta();
		
		meta.setDisplayName("Item Trading Info");
		
		List<String> lore = new ArrayList<String>();
		Database DB = Trade.instance.getRDatabase();

		lore.add(ChatColor.GRAY + "-----------------------");
		
		lore.add(ChatColor.GOLD+ "Lowest Price");
		Float lowestPrice = 0f;
		lowestPrice = DB.getLowestPrice(selectedItem.getType().name());
		lore.add(Float.toString(lowestPrice));
		
		lore.add(ChatColor.YELLOW+"Average Trading Price");
		Float averagePrice= 0f;
		averagePrice = DB.getAverageTrading(material.name());
		lore.add(Float.toString(averagePrice));
		
		lore.add(ChatColor.GOLD+"Trading Amount");
		int tradingAmount=0;
		tradingAmount = DB.getProductCountMaterial(material.name());
		lore.add(Integer.toString(tradingAmount));
		
		meta.setLore(lore);
		priceItem.setItemMeta(meta);
		
		inventory.setItem(infoPriceSlot, priceItem);

		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", infoBackSlot);
		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", infoExitSlot);
		player.openInventory(inventory);
	}
	
	
	
	

	
	// FLAG MENU________________________________________

	
	static void itemList(List<Product> productList, Inventory inventory, int page, int status, Database DB) {
		int numb = 0;
		if (productList != null)
			if (!productList.isEmpty())
				for (Product product : productList) {

					if (status > -1) {
						int id = product.getId();
						Float price = product.getPrice();

						java.util.Date creation_time = null;
						try {
							creation_time = Database.format.parse(product.getCreation_time());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						java.util.Date present_time = new java.util.Date();
						long diff = present_time.getTime() - creation_time.getTime();

						float period = Trade.instance.getConfig().getInt("regist_period");
						float wait_minute = Trade.instance.getConfig().getInt("wait_time");

						float remain_second = (period) * 60 * 60 + wait_minute * 60 - (diff / 1000);
						float remain_minute = (remain_second / 60);
						float remain_hour = (remain_minute / 60);

						ItemStack item = ItemSerializer.stringToItem(product.getItem());
						ItemMeta meta = item.getItemMeta();
						List<String> lore = new ArrayList<String>();
						String sold = "";
						if (product.getStatus() == 1) {
							sold = ChatColor.YELLOW + "Sold Out";
						} else if (product.getStatus() == 0) {
							if ((remain_second) < 0) {
								sold = ChatColor.RED + "Failed";
							} else if ((remain_second) > (period) * 60 * 60) {
								sold = ChatColor.YELLOW + "Waiting";
							} else {
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
						lore.add(ChatColor.YELLOW + String.format("%dH %dM", (int) remain_hour, (int) remain_minute % 60));
						lore.add(ChatColor.WHITE + "Price");
						lore.add(ChatColor.YELLOW + Float.toString(price));

						if (status > 0) {
							lore.add(ChatColor.WHITE + "Status");
							lore.add(sold);
							if (status > 1) {
								UUID uuid = null;
								String name = "Non Est";
								OfflinePlayer player = null;
								try {
									uuid = UUID.fromString(product.getUUID());
									player = Bukkit.getOfflinePlayer(uuid);
									name = player.getName();
								} catch (Exception e) {

								}
								lore.add("owner");
								lore.add(name);

								lore.add(ChatColor.YELLOW + "[SHIFT + LEFT_CLICK]" + ChatColor.WHITE + "BAN TOGGLE");
								lore.add(ChatColor.YELLOW + "[LEFT_CLICK]" + ChatColor.WHITE + "ITEM BUY");
								lore.add(ChatColor.YELLOW + "[SHIFT + RIGHT_CLICK]" + ChatColor.WHITE + "ITEM DROP");
							}
						}

						lore.add(ChatColor.BLACK + "Product ID");
						lore.add(ChatColor.BLACK + Integer.toString(id));

						ItemStack button = item;
						meta.setLore(lore);
						button.setItemMeta(meta);

						inventory.setItem(numb, button);
					}else {
						ItemStack item = ItemSerializer.stringToItem(product.getItem());
						GUIManager.setButton(inventory, item.getType(), item.getType().name(), numb);
					}

					numb++;
				}
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.RED + "Back Page", buyPageBackSlot);
		GUIManager.setButton(inventory, Material.HEART_OF_THE_SEA, Integer.toString(page), buyPageSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GREEN + "Next Page", buyPageNextSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", buyBackSlot);
		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", buyExitSlot);
	}

}