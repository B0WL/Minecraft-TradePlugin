package intenrnal;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemManager {
	HashMap<UUID,String> playerCache = new HashMap<UUID,String>();

	HashMap<Long,StockedItem> auctionCache =new HashMap<Long,StockedItem>();
	HashMap<Long,ItemStack> itemCache = new HashMap<Long,ItemStack>();

	void playerJoin(Player player) {
		if(getPlayer(player.getUniqueId())== null) {
			playerCache.put(player.getUniqueId(), player.getDisplayName());
		}
	}
	
	String getPlayer(UUID id) {
		return playerCache.get(id);
	}
	
	void itemRegister(Player player,ItemStack item, double price, int amount) {
		long itemid = itemCache.size()+1;
		long auctionid = auctionCache.size()+1;
		
		StockedItem stock 
		= new StockedItem(
				player.getUniqueId()
				,auctionid
				,itemid
				,price
				,amount);

		itemCache.put(itemid, item);
		auctionCache.put(auctionid, stock);
	}
}


class StockedItem {
	enum AuctionState{
		SALE,COMPLETE
	}
	UUID owner;
	long id;
	long itemid;
	
	Date registDate;
	AuctionState state;
	
	double price;
	int amount;
	
	StockedItem (UUID owner, long id, long itemid, double price, int amount){
		this.owner = owner;
		this.id = id;
		this.itemid= itemid;
		
		this.registDate = new Date();
		this.state = AuctionState.SALE;
		
		this.price = price;
		this.amount = amount;
		
	}
}


