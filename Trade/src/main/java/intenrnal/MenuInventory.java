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
	public static int mainBuySlot = 11;
	public static int mainSellSlot = 13;
	public static int mainListSlot = 15;
	public static int mainExitSlot = 26;

	public static void onAuctionMain(Player player) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Auction : Main");

		GUIManager.setButton(inventory, Material.DIAMOND_BLOCK, ChatColor.GREEN + "Item Buy", mainBuySlot);
		GUIManager.setButton(inventory, Material.GOLD_BLOCK, ChatColor.YELLOW + "Item Sell", mainSellSlot);
		GUIManager.setButton(inventory, Material.BOOK, ChatColor.BLUE + "Trade List", mainListSlot);
		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", mainExitSlot);

		player.openInventory(inventory);
	}

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

	public static int priceItemSlot = 13;
	public static int priceConfirmSlot = 26;

	public static void onAuctionPrice(Player player, int price, ItemStack item) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Price : " + String.valueOf(price));

		GUIManager.setButton(inventory, Material.IRON_NUGGET, ChatColor.RED + "Down 1", 10);
		GUIManager.setButton(inventory, Material.IRON_INGOT, ChatColor.RED + "Down 10", 11);
		GUIManager.setButton(inventory, Material.IRON_BLOCK, ChatColor.RED + "Down 100", 12);

		inventory.setItem(priceItemSlot, item);

		GUIManager.setButton(inventory, Material.GOLD_BLOCK, ChatColor.GREEN + "UP 100", 14);
		GUIManager.setButton(inventory, Material.GOLD_INGOT, ChatColor.GREEN + "UP 10", 15);
		GUIManager.setButton(inventory, Material.GOLD_NUGGET, ChatColor.GREEN + "UP 1", 16);

		GUIManager.setButton(inventory, Material.SUNFLOWER, ChatColor.GREEN + "Confirm", priceConfirmSlot);

		player.openInventory(inventory);
	}

	public static int checkItemSlot = 11;
	public static int checkDropSlot = 15;
	public static int checkBackSlot = 18;
	
	public static void onAuctionCheckDrop(Player player, ItemStack item, String price, String id, boolean isDrop) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 27, "Drop : " +item.getItemMeta().getDisplayName());
		inventory.setItem(checkItemSlot, item);
		
		String isDropString = "";
		if(isDrop) {
			isDropString = "Drop";
		}else {
			isDropString = "Delete";
		}
		
		GUIManager.setButton(inventory, Material.TNT, ChatColor.RED+ isDropString+" this item", checkDropSlot);
		
		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", checkBackSlot);
		player.openInventory(inventory);
	}
	
	
	
	
	
	public static int buyPageBackSlot = 48;
	public static int buyPageSlot = 49;
	public static int buyPageNextSlot = 50;
	public static int buyBackSlot = 45;
	public static int buyExitSlot = 53;

	public static void onAuctionBuy(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Buy");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page);

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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					java.util.Date present_time = new java.util.Date();
					long diff = present_time.getTime() - creation_time.getTime();

					int period = Trade.instance.getConfig().getInt("Regist_period");
					String hour = String.valueOf(period - (int)(diff/1000/60/60));

					ItemStack item = ItemSerializer.stringToItem(product.getItem());
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();

					if (meta.getLore() != null)
						lore.addAll(meta.getLore());

					lore.add("-----------------------");
					lore.add(ChatColor.WHITE +"Remain Hour");
					lore.add(ChatColor.YELLOW + hour+"hour");
					lore.add(ChatColor.WHITE +"Price");
					lore.add(ChatColor.YELLOW +Integer.toString(price));
					lore.add(ChatColor.BLACK +"Product ID");
					lore.add(ChatColor.BLACK +Integer.toString(id));

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

		player.openInventory(inventory);
	}
	
	
	
	
	
	public static void onAuctionList(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : List");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemUser(player,page);

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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					java.util.Date present_time = new java.util.Date();
					long diff = present_time.getTime() - creation_time.getTime();
					int period = Trade.instance.getConfig().getInt("Regist_period");

					String hour = String.valueOf(period - (int)(diff/1000/60/60));

					ItemStack item = ItemSerializer.stringToItem(product.getItem());
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					String sold = "";
					if(product.getSold() == 1) {
						sold =ChatColor.YELLOW+ "Sold Out";
					}else if(Integer.parseInt(hour)<0){
						sold =ChatColor.RED+ "Failed";
					}else if(product.getSold() == 0){
						sold =ChatColor.GREEN+ "On Sale";
					}else if(product.getSold() ==2) {
						sold = ChatColor.DARK_RED+"Stop Sale";
					}else if(product.getSold() ==3) {
						sold = ChatColor.DARK_RED+"Confiscation";
					}else {
						sold = "error";
					}

					if (meta.getLore() != null)
						lore.addAll(meta.getLore());

					lore.add(ChatColor.GRAY +"-----------------------");
					lore.add(ChatColor.WHITE +"Remain Hour");
					lore.add(ChatColor.YELLOW + hour+"hour");
					lore.add(ChatColor.WHITE +"Price");
					lore.add(ChatColor.YELLOW +Integer.toString(price));
					lore.add(ChatColor.WHITE +"Status");
					lore.add(sold);
					lore.add(ChatColor.BLACK +"Product ID");
					lore.add(ChatColor.BLACK +Integer.toString(id));

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

		player.openInventory(inventory);
		
	}
	
	
	
	
	
	

}


















