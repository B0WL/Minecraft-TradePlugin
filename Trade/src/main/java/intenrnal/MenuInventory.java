package intenrnal;

import java.util.ArrayList;
import java.util.List;

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

	public static int buyPageBackSlot = 48;
	public static int buyPageSlot = 49;
	public static int buyPageNextSlot = 50;
	public static int buyBackSlot = 45;
	public static int buyExitSlot = 53;
	public static void onAuctionBuy(Player player, int page) {
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(), 54, "Auction : Buy");
		Database DB = Trade.instance.getRDatabase();

		List<Product> productList = DB.listItemAll(page);
		
		int numb=0;
		for(Product product : productList) {
			int id = product.getId();
			int price = product.getPrice();
			ItemStack item =ItemSerializer.stringToItem(product.getItem());
			List<String> lore = new ArrayList<String>();
			
			lore.add("Product ID");
			lore.add(Integer.toString(id));
			lore.add("Price");
			lore.add(Integer.toString(price));
			
			GUIManager.setProduct(inventory,item,numb,lore);
			numb++;
		}

		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.RED + "Back Page", buyPageBackSlot);
		GUIManager.setButton(inventory, Material.HEART_OF_THE_SEA, ChatColor.BLUE +Integer.toString(page), buyPageSlot);
		GUIManager.setButton(inventory, Material.SUNFLOWER, ChatColor.GREEN + "Next Page", buyPageNextSlot);

		GUIManager.setButton(inventory, Material.SLIME_BALL, ChatColor.GRAY + "Back", buyBackSlot);
		GUIManager.setButton(inventory, Material.BARRIER, ChatColor.RED + "Exit", buyExitSlot);
		

		player.openInventory(inventory);
	}

	

	

}
