package util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemSerializer {

	
	
	public static String itemToString(ItemStack item) {
		YamlConfiguration itemConfig =new YamlConfiguration();
		itemConfig.set("item", item);
		String string = itemConfig.saveToString();
		return string;
	}
	
	public static ItemStack stringToItem(String string) {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.loadFromString(string);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		ItemStack item = config.getItemStack("item");
		return item;
	}
	
	
	
	
	
}
