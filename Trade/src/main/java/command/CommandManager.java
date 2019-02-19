package command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import intenrnal.MenuInventory;
import main.Trade;


public class CommandManager implements CommandExecutor {
	private Trade trade;
	private MenuInventory menuInventory = new MenuInventory();
	
	public CommandManager(Trade trade) {
		this.trade = trade;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length == 0) {
			return true;
		}
		
		if(args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(Trade.CHAT_PREFIX + " Commands:");
			sender.sendMessage(ChatColor.WHITE + "/" + label + " Auction" + ChatColor.GRAY + " open Auction");
	    	
	    	return true;
		}
	    
	    
	    if(args[0].equalsIgnoreCase("Auction")) {
	    	sender.sendMessage("Auction open");
	    	menuInventory.onAuctionMain((Player)sender);
	    	return true;
	    }
	    
	    
	    
	    
	    
	    
	    
	    return false;
	    
	    
	}



}