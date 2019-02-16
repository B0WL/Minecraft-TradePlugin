package main;


import org.bukkit.plugin.java.JavaPlugin;

import command.TradeCommandExecutor;


public class Trade extends JavaPlugin{



	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
		this.getCommand("addsaf").setExecutor(new TradeCommandExecutor(this));
		
		getLogger().info("Trade onEnable");
		
	}

	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		getLogger().info("Trade onDisable");
	}



}
