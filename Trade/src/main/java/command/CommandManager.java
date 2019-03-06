package command;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import intenrnal.MenuInventory;
import main.Trade;
import util.ItemSerializer;

public class CommandManager implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 0) {
			return true;
		}

		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(Trade.CHAT_PREFIX + " Commands:");
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
			

		}

		return false;

	}

}