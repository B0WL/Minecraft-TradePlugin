package command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import intenrnal.MenuInventory;
import main.Trade;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import util.ItemSerializer;

public class CommandManager implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 0) {
			return true;
		}

		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(" Commands:");
			sender.sendMessage(ChatColor.WHITE + "/" + label + " Auction" + ChatColor.GRAY + " open Auction");

			return true;
		}

		if (args[0].equalsIgnoreCase("Auction")) {
			sender.sendMessage("Auction open");
			MenuInventory.onAuctionMain((Player) sender);
			return true;
		}

		if (args[0].equalsIgnoreCase("nbt")) {
			sender.sendMessage("nbt command");
			Player player = (Player) sender;

			ItemStack item = player.getInventory().getItemInMainHand();
			
			
			String string = ItemSerializer.itemToString(item);
			sender.sendMessage(string);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("eco")) {
			Economy econ = Trade.instance.getEconomy();
			Player player = (Player) sender;
			
			sender.sendMessage(String.format("You have %s", econ.format(econ.getBalance(player.getName()))));
            EconomyResponse r = econ.depositPlayer(player, 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were given %s and now have %s", econ.format(r.amount), econ.format(r.balance)));
            } else {
                sender.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
			
			return true;
		}
		
		

		return false;

	}

}