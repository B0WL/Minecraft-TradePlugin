package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.Trade;


public class CommandManager implements CommandExecutor {
	private Trade trade;
	
	public CommandManager(Trade trade) {
		this.trade = trade;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
	    if (cmd.getName().equalsIgnoreCase("KillPlayer")) {
	        Player target = sender.getServer().getPlayer(args[0]);
	         // Make sure the player is online.
	        if (target == null) {
	            sender.sendMessage(args[0] + " is not currently online.");
	            return true;
	        }
	        target.setHealth(0.0D); 
	    }
	    return false;
	    
	    
	}



}