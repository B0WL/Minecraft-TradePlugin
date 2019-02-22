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
    	Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(),54,"Auction");

    	setButton(inventory,Material.DIAMOND_BLOCK,ChatColor.GREEN+"Item Buy",20);
    	setButton(inventory,Material.GOLD_BLOCK,ChatColor.YELLOW+"Item Sell",22);
    	setButton(inventory,Material.BOOK,ChatColor.BLUE+"Trade List",24);
    	setButton(inventory,Material.BARRIER,ChatColor.RED+"Exit",53);
    	
    	player.openInventory(inventory);
	}
	
	public void onAuctionSell(Player player, ItemStack item, int price) {
    	Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(),54,"Auction : Sell");
    	
    	ItemMeta meta =null;
    	meta = new ItemStack(Material.STONE).getItemMeta();
    	ItemStack selectedItem = new ItemStack(Material.CHEST);
    	if(item != null) {
        	selectedItem = item;
    	}else {
    		meta.setDisplayName(ChatColor.GRAY+"Item Select");
    		selectedItem.setItemMeta(meta);
    	}
    	inventory.setItem(10, selectedItem);
    	
    	setButton(inventory,Material.GOLD_BLOCK,String.valueOf(price),19);
    	setButton(inventory,Material.BOOK,ChatColor.GREEN+"Register",28);
    	setButton(inventory,Material.SLIME_BALL,ChatColor.GRAY+"Back",45);
    	setButton(inventory,Material.BARRIER,ChatColor.RED+"Exit",53);
    	
    	player.openInventory(inventory);
    	
	}
	
	public void onAuctionPrice(Player player,  int price, ItemStack item) {
    	Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(),27,"Price : "+String.valueOf(price));

    	setButton(inventory,Material.IRON_NUGGET,ChatColor.RED+"Down 1",10);
    	setButton(inventory,Material.IRON_INGOT,ChatColor.RED+"Down 10",11);
    	setButton(inventory,Material.IRON_BLOCK,ChatColor.RED+"Down 100",12);
    	
    	inventory.setItem(13, item);
    	
    	setButton(inventory,Material.GOLD_BLOCK,ChatColor.GREEN+"UP 100",14);
    	setButton(inventory,Material.GOLD_INGOT,ChatColor.GREEN+"UP 10",15);
    	setButton(inventory,Material.GOLD_NUGGET,ChatColor.GREEN+"UP 1",16);
    	
    	setButton(inventory,Material.SUNFLOWER,ChatColor.GREEN+"Confirm",26);

    	player.openInventory(inventory);
	}
	
	
	
	private void setButton(Inventory inventory,Material icon, String name, int slot) {
		ItemStack button = new ItemStack(icon);
		ItemMeta meta = button.getItemMeta();
		meta.setDisplayName(name);
		button.setItemMeta(meta);
		
		inventory.setItem(slot, button);
	}
	
	

}
