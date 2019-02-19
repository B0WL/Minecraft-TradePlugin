package intenrnal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuInventory {	
	
	public void onAuctionMain(Player player) {
    	Inventory inventory = Bukkit.createInventory(null,54,"Auction");

    	ItemStack buyButton = new ItemStack(Material.DIAMOND_BLOCK);
    	ItemStack sellButton = new ItemStack(Material.GOLD_BLOCK);
    	ItemStack listButton = new ItemStack(Material.BOOK);
    	ItemStack exitButton = new ItemStack(Material.TNT);
    	
    	ItemMeta meta = buyButton.getItemMeta();
    	meta.setDisplayName(ChatColor.GREEN+"Item Buy");
    	buyButton.setItemMeta(meta);

    	meta.setDisplayName(ChatColor.YELLOW+"Item Sell");
    	sellButton.setItemMeta(meta);

    	meta.setDisplayName(ChatColor.BLUE+"Trade List");
    	listButton.setItemMeta(meta);
    	
    	meta.setDisplayName(ChatColor.RED+"Exit");
    	exitButton.setItemMeta(meta);

    	inventory.setItem(20, buyButton);
    	inventory.setItem(22, sellButton);
    	inventory.setItem(24, listButton);
    	inventory.setItem(53, exitButton);
    	
    	player.openInventory(inventory);
	}
	
	public void onAuctionSell(Player player) {
    	Inventory inventory = Bukkit.createInventory(null,54,"Auction : Sell");

    	ItemStack selectButton = new ItemStack(Material.ANVIL);
    	ItemStack registerButton = new ItemStack(Material.BOOK);
    	ItemStack backButton = new ItemStack(Material.SIGN);
    	ItemStack exitButton = new ItemStack(Material.TNT);
    	
    	ItemMeta meta = selectButton.getItemMeta();
    	meta.setDisplayName(ChatColor.YELLOW+"Item Select");
    	selectButton.setItemMeta(meta);

    	meta.setDisplayName(ChatColor.GREEN+"Register");
    	registerButton.setItemMeta(meta);

    	meta.setDisplayName(ChatColor.GRAY+"Back");
    	backButton.setItemMeta(meta);
    	
    	meta.setDisplayName(ChatColor.RED+"Exit");
    	exitButton.setItemMeta(meta);

    	inventory.setItem(10, selectButton);
    	inventory.setItem(28, registerButton);
    	inventory.setItem(45, backButton);
    	inventory.setItem(53, exitButton);
    	
    	player.openInventory(inventory);
	}
	
	
	public void onItemSelect(Player player) {

    	Inventory inventory = Bukkit.createInventory(null,InventoryType.ANVIL,"Auction : Select");
    	

    	ItemStack selectButton = new ItemStack(Material.DIAMOND_BLOCK);
    	ItemStack cancelButton = new ItemStack(Material.TNT);
    	
    	ItemMeta meta = selectButton.getItemMeta();
    	meta.setDisplayName(ChatColor.GREEN+"Confirm");
    	selectButton.setItemMeta(meta);

    	meta.setDisplayName(ChatColor.RED+"CANCEL");
    	cancelButton.setItemMeta(meta);
    	
    	

    	inventory.setItem(1, selectButton);
    	inventory.setItem(2, cancelButton);
    	
    	player.openInventory(inventory);
    	
    	
	}
	

}
