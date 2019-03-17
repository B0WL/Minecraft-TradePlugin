package main;


import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import command.CommandManager;
import database.Database;
import database.SQLite;
import intenrnal.MenuInventory;
import listener.InventoryListener;
import net.milkbowl.vault.economy.Economy;

public class Trade extends JavaPlugin {
	public static Trade instance;

	private Database db;
    private static Economy econ = null;

	@Override
	public void onEnable() {
		instance =this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		
		if(!setupEconomy()) {
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			db = new SQLite(this);
			db.load();
		} catch (Exception e) {
			getLogger().info("Database Load Failed");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		
		
		try {
			// getServer().getPluginManager().registerEvents(new ItemListener(), this);
			getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		} catch (Exception e) {
			getLogger().info("Inventory Listen Failed");
			getServer().getPluginManager().disablePlugin(this);
		}
		try {
			this.getCommand("Trade").setExecutor(new CommandManager());
		} catch (Exception e) {
			getLogger().info("Command Listen Failed");
			getServer().getPluginManager().disablePlugin(this);
		}
		getLogger().info("Trade onEnable");

	}

	@Override
	public void onDisable() {
		getLogger().info("Trade onDisable");
	}
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().info("Vault Load Failed");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
			getLogger().info("rsp Load Failed");			
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	public Database getRDatabase() {
		return this.db;
	}
	public Economy getEconomy() {
        return econ;
    }

}