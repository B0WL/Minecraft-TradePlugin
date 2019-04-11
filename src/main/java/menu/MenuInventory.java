package menu;

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
import net.milkbowl.vault.economy.Economy;
import util.GUIManager;
import util.ItemSerializer;
import util.RecordManager;

public class MenuInventory {

	// FLAG MENU_MAIN
	public static final int mainReadmeSlot = 0;
	public static final int mainManagerSlot = 1;
	public static final int mainRecordSlot = 2;
	public static final int mainMoneySlot = 8;
	
	public static final int mainBuySlot = 11;
	public static final int mainSellSlot = 12;
	public static final int mainPriceSlot = 13;
	
	public static final int mainListSlot = 15;
	public static final int mainExitSlot = 26;

	public static void onMain(Player player) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.MAIN), 27, "Auction : Main");
		ItemMeta meta = null;
		ItemStack readMe = new ItemStack(Material.PAPER);
		meta = readMe.getItemMeta();
		meta.setDisplayName("Trade - Auction");//TODO READ ME Àû±â
		
		List<String> lore = new ArrayList<String>();
		meta.setLore(lore);
		
		readMe.setItemMeta(meta);
		inventory.setItem(mainReadmeSlot, readMe);
		
		Economy econ = Trade.instance.getEconomy();
		GUIManager.setButton(inventory, Material.GOLD_INGOT, ChatColor.YELLOW +String.format("You have %s", econ.format(econ.getBalance(player.getName()))), mainMoneySlot);

		GUIManager.setButton(inventory, Material.DIAMOND_BLOCK, ChatColor.GREEN + "Item Buy", mainBuySlot);
		GUIManager.setButton(inventory, Material.GOLD_BLOCK, ChatColor.YELLOW + "Item Sell", mainSellSlot);
		GUIManager.setButton(inventory, Material.SIGN, ChatColor.GOLD + "Market Price", mainPriceSlot);
		
		GUIManager.setButton(inventory, Material.BOOK, ChatColor.BLUE + "Trade List", mainListSlot);

		if (player.hasPermission("auction.manager")) {
			GUIManager.setButton(inventory, Material.ANVIL, ChatColor.WHITE + "Management", mainManagerSlot);
			GUIManager.setButton(inventory, Material.BOOKSHELF, ChatColor.WHITE + "Record", mainRecordSlot);
		}

		GUIManager.setButton(inventory, Material.TNT, ChatColor.RED + "Exit", mainExitSlot);

		player.openInventory(inventory);
	}

	// FLAG MENU_SELL
	public static final int sellItemSlot = 11;
	public static final int sellPriceSlot = 12;
	public static final int sellRegistSlot = 15;
	public static final int sellBackSlot = 18;
	public static final int sellExitSlot = 26;
	
	public static void onSell(Player player) {
		onSell(player, null, BigDecimal.ZERO, 0);
	}
	public static void onSell(Player player, ItemStack item, BigDecimal price , int amount) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.SELL), 27, "Auction : Sell");

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
		GUIManager.setButton(inventory, Material.TNT, ChatColor.RED + "Exit", sellExitSlot);
		player.openInventory(inventory);

	}

	// FLAG MENU_PRICE
	public static final int priceItemSlot = 13;
	public static final int priceConfirmSlot = 26;
	public static final int priceUnitSlot[] = {4,22};
	public static final int priceDownSlot[] = { 10, 11, 12 };
	public static final int priceUpSlot[] = { 16, 15, 14 };

	public static void onPrice(Player player, BigDecimal price, ItemStack item, BigDecimal priceUnit) {
		Inventory inventory = Bukkit.createInventory(
				new MenuInventoryHolder(MenuHolder.PRICE, price), 27, item.getItemMeta().getDisplayName()+" Price : " + String.valueOf(price));
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

		GUIManager.setButton(inventory, Material.GOLDEN_APPLE, ChatColor.GREEN + "Confirm", priceConfirmSlot);

		player.openInventory(inventory);
	}

	// FLAG MENU_DROP
	public static final int checkItemSlot = 10;
	public static final int checkDownSlot[] = { 12, 13 };
	public static final int checkAmountSlot = 14;
	public static final int checkUpSlot[] = { 16, 15 };
	public static final int checkDropSlot = 26;
	public static final int checkBackSlot = 18;

	public static void onCheck(Player player, ItemStack item) {
		String statusTitle = "";

		Material confirm = null;

		statusTitle = "Drop";
		confirm = Material.TNT;

		String name = "";

		if (item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else
			name = item.getType().name();

		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.DROP), 27, statusTitle + " : " + name);
		inventory.setItem(checkItemSlot, item);

		GUIManager.setButton(inventory, confirm, ChatColor.RED + statusTitle + " this item", checkDropSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", checkBackSlot);
		player.openInventory(inventory);
	}

	public static void onCheck(Player player, ItemStack item, int amount, Float price) {
		String statusTitle = "";

		Material confirm = null;
		statusTitle = "Confirm";
		confirm = Material.GOLDEN_APPLE;

		String name = "";

		if (item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else
			name = item.getType().name();

		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.CONFIRM), 27, 
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
	public static final int listBackSlot = 45;
	public static final int listPageBackSlot = 48;
	public static final int listPageSlot = 49;
	public static final int listPageNextSlot = 50;
	public static final int listFindSlot = 51;
	public static final int listExitSlot = 53;	
	
	public static void onBuy(Player player) {
		onBuy(player, 1);
	}
	public static void onBuy(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.BUY), 54, "Auction : Buy");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page, player);
		
		itemList(productList, inventory, page, 0, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", listFindSlot);
		player.openInventory(inventory);
	}
	
	//FLAG MENU_BUY_MAT
	public static void onBuy(Player player,String material) {
		onBuy(player, 1,material);
	}
	public static void onBuy(Player player, int page, String material) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.BUY_MAT), 54, "Auction : Buy - "+material);
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page, player,material);
		
		itemList(productList, inventory, page, 0, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", listFindSlot);
		player.openInventory(inventory);
	}
	
	// FLAG MENU_LIST
	public static void onList(Player player) {
		onList(player, 1);
	}
	public static void onList(Player player, int page) {
		Database DB = Trade.instance.getRDatabase();
		int regiNumb = Trade.instance.getConfig().getInt("register_number");
		int haveNumb = DB.getProductCount(player);

		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.LIST), 54,
				String.format("Auction : List [%d/%d]", haveNumb, regiNumb));

		List<Product> productList = DB.listItemUser(player, page);
		itemList(productList, inventory, page, 1, DB);
		player.openInventory(inventory);

	}
	
	// FLAG MENU_MANAGER
	public static void onManager(Player player) {
		onManager(player, 1);
	}
	public static void onManager(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.MANAGER), 54, "Auction : Manager");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page);
		
		RecordManager.record("debug", ""+ productList.size());
		
		itemList(productList, inventory, page, 2, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", listFindSlot);
		
		player.openInventory(inventory);
	}
	
	// FLAG MENU_MANAGER_MAT
	public static void onManager(Player player, String material) {
		onManager(player, 1, material);
	}
	public static void onManager(Player player, int page, String material) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.MANAGER_MAT), 54, "Auction : Manager - "+material);
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page, material);
		itemList(productList, inventory, page, 2, DB);
		GUIManager.setButton(inventory, Material.ENDER_PEARL, "FIND ITEM", listFindSlot);
		
		player.openInventory(inventory);
	}

	// FLAG MENU_FIND
	public static void onFind(Player player) {
		onFind(player, 1);
	}
	public static void onFind(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.BUY_FIND), 54, "Auction : Find");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemGroupMaterial(page, player);
		findList(productList, inventory, page);
		player.openInventory(inventory);
	}	
	// FLAG MENU_FIND_MANAGER
	public static void onFindManager(Player player) {
		onFindManager(player, 1);
	}
	public static void onFindManager(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.MANAGER_FIND), 54, "Auction : Find - ALL");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemGroupMaterial(page);
		findList(productList, inventory, page);
		player.openInventory(inventory);
	}

	//FLAG MENU_RECORD_LIST
	public static void onRecordList(Player player) {
		onRecordList(player, 1);
	}
	public static void onRecordList(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.RECORD), 54, "Auction : Trading Record");
		Database DB = Trade.instance.getRDatabase();

		List<Product> recordList = DB.listRecordALL(page);
		recordList(recordList,inventory,page);
		player.openInventory(inventory);
	}
	
	
	//FLAG MENU_MARKET_PRICE
	public static void onMarketPrice(Player player) {
		onMarketPrice(player, 1);
	}
	public static void onMarketPrice(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(
				new MenuInventoryHolder(MenuHolder.MARKET_PRICE), 54, "Auction : Market Price");
		Database DB = Trade.instance.getRDatabase();

		List<Product> recordList = DB.listRecordGroupMaterial(page);
		findList(recordList,inventory,page);
		player.openInventory(inventory);
	}
	
	
	//FLAG MENU_ITEM_INFO
	public static final int infoMaterialSlot = 11;
	public static final int infoPriceSlot = 15;
	public static final int infoBackSlot = 18;
	public static final int infoExitSlot = 26;
	
	public static void onItemInfo(Player player, Material material) {
		String materialName = material.name();
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(MenuHolder.INFO, material.name()), 27, "Auction : Info - "+materialName);
		
		GUIManager.setButton(inventory, material, materialName, infoMaterialSlot);

		ItemMeta meta = null;
		ItemStack selectedItem = new ItemStack(material);
		meta = selectedItem.getItemMeta();
		
		ItemStack priceItem = new ItemStack(Material.BOOK);
		meta = priceItem.getItemMeta();
		
		meta.setDisplayName("Item Trading Info");
		
		List<String> lore = new ArrayList<String>();
		
		Database DB = Trade.instance.getRDatabase();
		
		Float lowestPrice = 0f;
		lowestPrice = DB.getLowestPrice(materialName);
		Float averagePrice= 0f;
		averagePrice = DB.getAverageTrading(materialName);
		int tradingAmount=0;
		tradingAmount = DB.getProductCountMaterial(materialName);

		lore.add(ChatColor.GRAY + "-----------------------");
		
		lore.add(ChatColor.GOLD+ "Lowest Price");
		lore.add(Float.toString(lowestPrice));
		
		lore.add(ChatColor.YELLOW+"Average Trading Price");
		lore.add(Float.toString(averagePrice));
		
		lore.add(ChatColor.GOLD+"Trading Amount");
		lore.add(Integer.toString(tradingAmount));
		
		meta.setLore(lore);
		priceItem.setItemMeta(meta);
		
		inventory.setItem(infoPriceSlot, priceItem);

		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back",  infoBackSlot);
		GUIManager.setButton(inventory, Material.TNT, ChatColor.RED + "Exit", infoExitSlot);
		player.openInventory(inventory);
	}
	
	
	
	
	// FLAG MENU________________________________________
	
	static void findList(List<Product> productList, Inventory inventory, int page) {
		int numb = 0;
		if (productList != null)
			if (!productList.isEmpty())
				for (Product product : productList) {
					String itemString = product.getItem();
					ItemStack item = ItemSerializer.stringToItem(itemString);
					GUIManager.setButton(inventory, item.getType(), item.getType().name(), numb);
					numb++;
				}

		listbasicButtons(inventory, page);
	}

	static void findRecordList(List<Product> productList, Inventory inventory, int page) {
		int numb = 0;
		if (productList != null)
			if (!productList.isEmpty())
				for (Product product : productList) {
					String itemString = product.getItem();
					ItemStack item = ItemSerializer.stringToItem(itemString);
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					
					
					String materialName = item.getType().name();

					Database DB = Trade.instance.getRDatabase();
					Float lowestPrice = 0f;
					lowestPrice = DB.getLowestPrice(materialName);
					Float averagePrice= 0f;
					averagePrice = DB.getAverageTrading(materialName);
					int tradingAmount=0;
					tradingAmount = DB.getProductCountMaterial(materialName);

					lore.add(ChatColor.GRAY + "-----------------------");
					lore.add(ChatColor.GOLD+ "Lowest Price");
					lore.add(Float.toString(lowestPrice));
					lore.add(ChatColor.YELLOW+"Average Trading Price");
					lore.add(Float.toString(averagePrice));
					lore.add(ChatColor.GOLD+"Trading Amount");
					lore.add(Integer.toString(tradingAmount));
					
					
					meta.setLore(lore);
					item.setItemMeta(meta);
					inventory.setItem(numb,item);
					numb++;
				}

		listbasicButtons(inventory, page);
	}

	static void itemList(List<Product> productList, Inventory inventory, int page, int status, Database DB) {/// status 0 = BUY, 1 = LIST, 2 = MANAGER
		int numb = 0;
		if (productList != null)
			if (!productList.isEmpty())
				for (Product product : productList) {

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
					
					String itemString =product.getItem();
					ItemStack item = ItemSerializer.stringToItem(itemString);
					
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
								uuid = UUID.fromString(product.getSeller());
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
					numb++;
				}


		listbasicButtons(inventory, page);
	}
	
	static void recordList(List<Product> list, Inventory inventory, int page) {
		int numb = 0;
		if (list != null)
			if (!list.isEmpty())
				for (Product product : list) {
					UUID sellerUUID = null;
					String sellerStringUUID = product.getSeller();
					String sellerName = "Non Est";
					OfflinePlayer sellerPlayer = null;
					try {
						sellerUUID = UUID.fromString(sellerStringUUID);
						sellerPlayer = Bukkit.getOfflinePlayer(sellerUUID);
						sellerName = sellerPlayer.getName();
					} catch (Exception e) {
					}
					String buyerStringUUID = product.getBuyer();
					UUID buyerUUID = null;
					OfflinePlayer buyerPlayer = null;
					String buyerName = "Non Est";
					try {
						buyerUUID = UUID.fromString(buyerStringUUID);
						buyerPlayer = Bukkit.getOfflinePlayer(buyerUUID);
						buyerName = buyerPlayer.getName();
					} catch (Exception e) {

					}
					int id = product.getId();
					Float price = product.getPrice();
					String time = product.getCreation_time();				
					

					String itemString = product.getItem();
					ItemStack item = ItemSerializer.stringToItem(itemString);
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					if (meta.getLore() != null)
						lore.addAll(meta.getLore());
					lore.add(ChatColor.GRAY + "-----------------------");
					
					lore.add("Seller");
					lore.add(sellerName);					
					lore.add("Buyer");
					lore.add(buyerName);
					
					lore.add(ChatColor.WHITE + "Price");
					lore.add(ChatColor.YELLOW + Float.toString(price));
					
					lore.add("Trading Time");
					lore.add(time);
					
					lore.add(ChatColor.BLACK + "Product ID");
					lore.add(ChatColor.BLACK + Integer.toString(id));

					ItemStack itemStack = item;
					meta.setLore(lore);
					itemStack.setItemMeta(meta);

					inventory.setItem(numb, itemStack);
					numb++;
				}
		listbasicButtons(inventory, page);
	}
	
	static void listbasicButtons(Inventory inventory, int page) {
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.RED + "Back Page", listPageBackSlot);
		GUIManager.setButton(inventory, Material.EGG, Integer.toString(page), listPageSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GREEN + "Next Page", listPageNextSlot);
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", listBackSlot);
		GUIManager.setButton(inventory, Material.TNT, ChatColor.RED + "Exit", listExitSlot);
	}
	
}