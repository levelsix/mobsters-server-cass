package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.BoosterDisplayItem;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class BoosterDisplayItemRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, BoosterDisplayItem> boosterDisplayItemIdsToBoosterDisplayItems;
	//key:booster pack id --> value:(key: booster item id --> value: booster item)
	private static Map<Integer, Map<Integer, BoosterDisplayItem>> 
		boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_BOOSTER_DISPLAY_ITEM;

	@Autowired
	protected BoosterDisplayItemEntityManager boosterDisplayItemEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	

	public Map<Integer, BoosterDisplayItem> getBoosterDisplayItemIdsToBoosterDisplayItems() {
		log.debug("retrieving all BoosterDisplayItems data map");
		if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
		}
		return boosterDisplayItemIdsToBoosterDisplayItems;
	}

	public Map<Integer, Map<Integer, BoosterDisplayItem>> getBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds() {
		if(null == boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();
		}
		return boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds;
	}

	public Map<Integer, BoosterDisplayItem> getBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackId(int boosterPackId) {
		log.debug("retrieve boosterPack data for boosterPack " + boosterPackId);
		if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
		}
		if (boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds == null) {
			//boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds = new HashMap<Integer, Map<Integer, BoosterDisplayItem>>();
			setStaticBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();
		}
		return boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.get(boosterPackId);
	}

	public BoosterDisplayItem getBoosterDisplayItemForBoosterDisplayItemId(int boosterDisplayItemId) {
		log.debug("retrieve boosterDisplayItem data for boosterDisplayItem " + boosterDisplayItemId);
		if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItems();      
		}
		return boosterDisplayItemIdsToBoosterDisplayItems.get(boosterDisplayItemId);
	}

	private void setStaticBoosterDisplayItemIdsToBoosterDisplayItems() {
		log.debug("setting  map of boosterDisplayItemIds to booster display items");
		
		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<BoosterDisplayItem> boosterDisplayItemList = getBoosterDisplayItemEntityManager().get().find(cqlquery);
		
		//fill up the map
		boosterDisplayItemIdsToBoosterDisplayItems = new HashMap<Integer, BoosterDisplayItem>();
		boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds =
				new HashMap<Integer, Map<Integer, BoosterDisplayItem>>();
		for(BoosterDisplayItem bdi : boosterDisplayItemList) {
			Integer intId = bdi.getId();
			boosterDisplayItemIdsToBoosterDisplayItems.put(intId, bdi);
			
			int packId = bdi.getBoosterPackId();
	        if(!boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.containsKey(packId)) {
	          Map<Integer, BoosterDisplayItem> bItemIdToBItem = new HashMap<Integer, BoosterDisplayItem>();
	          boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.put(packId, bItemIdToBItem);
	        }
	        //each itemId is unique (autoincrementing in the table)
	        Map<Integer, BoosterDisplayItem> itemIdToItem =
	            boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.get(packId);
	        itemIdToItem.put(bdi.getId(), bdi);
		}
	}
	
	public void setStaticBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds() {
		if (null == boosterDisplayItemIdsToBoosterDisplayItems) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItems();

		} else if (null == boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds) {
			boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds =
					new HashMap<Integer, Map<Integer, BoosterDisplayItem>>();
			for(BoosterDisplayItem bdi : boosterDisplayItemIdsToBoosterDisplayItems.values()) {
				int packId = bdi.getBoosterPackId();
				
				//check base case where no booster display items for booster pack is recorded
		        if(!boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.containsKey(packId)) {
		          Map<Integer, BoosterDisplayItem> bItemIdToBItem = new HashMap<Integer, BoosterDisplayItem>();
		          boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.put(packId, bItemIdToBItem);
		        }
		        //each itemId is unique (autoincrementing in the table)
		        Map<Integer, BoosterDisplayItem> itemIdToItem =
		            boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.get(packId);
		        itemIdToItem.put(bdi.getId(), bdi);
			}
		} // else, things should be set
	}



	public  void reload() {
		setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
	}
	

	
	
	public BoosterDisplayItemEntityManager getBoosterDisplayItemEntityManager() {
		return boosterDisplayItemEntityManager;
	}

	public void setBoosterDisplayItemEntityManager(
			BoosterDisplayItemEntityManager boosterDisplayItemEntityManager) {
		this.boosterDisplayItemEntityManager = boosterDisplayItemEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
