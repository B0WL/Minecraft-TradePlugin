package main;

import org.bukkit.ChatColor;

import org.bukkit.plugin.java.JavaPlugin;

import command.CommandManager;
import database.Database;
import database.SQLite;
import intenrnal.MenuInventory;
import listener.InventoryListener;

public class Trade extends JavaPlugin {

	public static final String CHAT_PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Trade"
			+ ChatColor.DARK_GREEN + "] " + ChatColor.GREEN;

	public static Trade instance;
	private Database db;

	@Override
	public void onEnable() {
		instance = this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		try {
			new MenuInventory();
		} catch (Exception e) {
			getLogger().info("menuGUI Load Failed");
		}
		try {
			// getServer().getPluginManager().registerEvents(new ItemListener(), this);
			getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		} catch (Exception e) {
			getLogger().info("Item Listen Failed");
		}

		try {
			this.getCommand("Trade").setExecutor(new CommandManager());
		} catch (Exception e) {
			getLogger().info("Command Listen Failed");
		}
		try {
			this.db = new SQLite(this);
			this.db.load();
		} catch (Exception e) {
			getLogger().info("Database Load Failed");
		}
		getLogger().info("Trade onEnable");

	}

	@Override
	public void onDisable() {
		getLogger().info("Trade onDisable");
	}

	public Database getRDatabase() {
		return this.db;
	}

}