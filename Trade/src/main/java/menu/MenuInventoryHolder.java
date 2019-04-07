package menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuInventoryHolder implements InventoryHolder {	
	private MenuHolder holder;
	private String material;
	
	public MenuInventoryHolder() {
		this.holder = MenuHolder.NULL;
	}
	public MenuInventoryHolder(MenuHolder holder) {
		this.holder = holder;
	}	
	public MenuInventoryHolder(MenuHolder holder , String material) {
		this.holder = holder;
		this.material =material;
	}
	
	public boolean holderIs(MenuHolder holder) {
		if(holder == this.holder) {
			return true;
		}else {
			return false;
		}
	}
	
	public MenuHolder getHolder() {
		return this.holder;
	}
	
	public String getMaterial() {
		return material;
	}

	@Override
	public Inventory getInventory() {	
		
		return null;
	}
	
	

}