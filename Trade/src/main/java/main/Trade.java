package main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import command.CommandManager;
import listener.InventoryListener;
import listener.ItemListener;

public class Trade extends JavaPlugin {

	public static final String CHAT_PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Trade" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN;

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub

		try {
			//getServer().getPluginManager().registerEvents(new ItemListener(), this);
			getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		} catch (Exception e) {
			getLogger().info("Item Listen Failed");
		}
		
		
		try {
			this.getCommand("Trade").setExecutor(new CommandManager(this));
		} catch (Exception e) {

			getLogger().info("Command Listen Failed");
		}

		getLogger().info("Trade onEnable");

	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		getLogger().info("Trade onDisable");
	}

}