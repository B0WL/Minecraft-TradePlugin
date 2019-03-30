package util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIManager {

	public static void setButton(Inventory inventory, Material icon, String name, int slot) {
		ItemStack button = new ItemStack( icon);
		ItemMeta meta = button.getItemMeta();
		meta.setDisplayName(name);
		button.setItemMeta(meta);

		inventory.setItem(slot, button);
	}

	

	
	
}
