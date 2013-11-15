package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.ItemEntityManager;
import com.lvl6.mobsters.po.Item;

@Component public class ItemRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, Item> idsToItems;

	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected ItemEntityManager itemEntityManager;

	public  Item getItemForId(UUID id) {
		log.debug("retrieve item data for id " + id);
		if (idsToItems == null) {
			setStaticIdsToItems();      
		}
		return idsToItems.get(id);
	}

	public  Map<UUID, Item> getItemsForIds(List<UUID> ids) {
		log.debug("retrieve items data for ids " + ids);
		if (idsToItems == null) {
			setStaticIdsToItems();      
		}
		Map<UUID, Item> toreturn = new HashMap<UUID, Item>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToItems.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToItems() {
		log.debug("setting  map of itemIds to items");

		String cqlquery = "select * from item;"; 
		List <Item> list = getItemEntityManager().get().find(cqlquery);
		idsToItems = new HashMap<UUID, Item>();
		for(Item c : list) {
			UUID id= c.getId();
			idsToItems.put(id, c);
		}
	}

	public Item findMatchingKeyToChest(int chestType) {
		String cqlquery = "select * from item;"; 
		List <Item> list = getItemEntityManager().get().find(cqlquery);
		Item i2 = new Item();
		for(Item i : list) {
			if(i.getItemType() == chestType) {
				i2 = i;
			}
			
		}
		return i2;
	}
	
	public Item getItemAccordingToName(UUID itemId) {
		if(idsToItems == null) {
			setStaticIdsToItems();
		}
		return idsToItems.get(itemId);
	}
	
	

	public  void reload() {
		setStaticIdsToItems();
	}
	
	

	public ItemEntityManager getItemEntityManager() {
		return itemEntityManager;
	}

	public void setItemEntityManager(
			ItemEntityManager itemEntityManager) {
		this.itemEntityManager = itemEntityManager;
	}
}
