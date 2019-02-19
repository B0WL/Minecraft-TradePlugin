package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import main.Trade;


public class TradeCommandExecutor implements CommandExecutor {
	private Trade trade;
	
	public TradeCommandExecutor(Trade trade) {
		this.trade = trade;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}



}
