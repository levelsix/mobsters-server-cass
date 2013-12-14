package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.BoosterItem;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class BoosterItemRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, BoosterItem> boosterItemIdsToBoosterItems;
	//key:booster pack id --> value:(key: booster item id --> value: booster item)
	private static Map<Integer, Map<Integer, BoosterItem>> 
		boosterItemIdsToBoosterItemsForBoosterPackIds;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_BOOSTER_ITEM;

	@Autowired
	protected BoosterItemEntityManager boosterItemEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	


	public Map<Integer, BoosterItem> getBoosterItemIdsToBoosterItems() {
		log.debug("retrieving all BoosterItems data map");
		if (boosterItemIdsToBoosterItems == null) {
			setStaticBoosterItemIdsToBoosterItems();
		}
		return boosterItemIdsToBoosterItems;
	}

	public Map<Integer, Map<Integer, BoosterItem>> getBoosterItemIdsToBoosterItemsForBoosterPackIds() {
		if(null == boosterItemIdsToBoosterItemsForBoosterPackIds) {
			setStaticBoosterItemIdsToBoosterItemsForBoosterPackIds();
		}
		return boosterItemIdsToBoosterItemsForBoosterPackIds;
	}

	public Map<Integer, BoosterItem> getBoosterItemIdsToBoosterItemsForBoosterPackId(int boosterPackId) {
		try {
			log.debug("retrieve boosterPack data for boosterPack " + boosterPackId);
			if (boosterItemIdsToBoosterItems == null) {
				setStaticBoosterItemIdsToBoosterItems();
			}
			if (boosterItemIdsToBoosterItemsForBoosterPackIds == null) {
				boosterItemIdsToBoosterItemsForBoosterPackIds = new HashMap<Integer, Map<Integer, BoosterItem>>();
			}
			List<BoosterItem> bis = new ArrayList<BoosterItem>(boosterItemIdsToBoosterItems.values());
			for(BoosterItem bi : bis) {
				int packId = bi.getBoosterPackId();
				if(!boosterItemIdsToBoosterItemsForBoosterPackIds.containsKey(packId)) {
					Map<Integer, BoosterItem> bItemIdToBItem = new HashMap<Integer, BoosterItem>();
					boosterItemIdsToBoosterItemsForBoosterPackIds.put(packId, bItemIdToBItem);
				}
				//each itemId is unique (autoincrementing in the table)
				Map<Integer, BoosterItem> itemIdToItem =
						boosterItemIdsToBoosterItemsForBoosterPackIds.get(packId);
				itemIdToItem.put(bi.getId(), bi);
			}
			return boosterItemIdsToBoosterItemsForBoosterPackIds.get(boosterPackId);
		} catch (Exception e) {
			log.error("error creating a map of booster item ids to booster items.", e);
		}
		return null;
	}

	public BoosterItem getBoosterItemForBoosterItemId(int boosterItemId) {
		log.debug("retrieve boosterItem data for boosterItem " + boosterItemId);
		if (boosterItemIdsToBoosterItems == null) {
			setStaticBoosterItemIdsToBoosterItems();      
		}
		return boosterItemIdsToBoosterItems.get(boosterItemId);
	}
 

	public void setStaticBoosterItemIdsToBoosterItemsForBoosterPackIds() {
		try {
			log.debug("setting static map of boosterPackId to (boosterItemIds to boosterItems) ");
			if (boosterItemIdsToBoosterItems == null) {
				setStaticBoosterItemIdsToBoosterItems();      
			}

			boosterItemIdsToBoosterItemsForBoosterPackIds = new HashMap<Integer, Map<Integer, BoosterItem>>();
			List<BoosterItem> bis = new ArrayList<BoosterItem>(boosterItemIdsToBoosterItems.values());
			for(BoosterItem bi : bis) {
				int packId = bi.getBoosterPackId();
				if(!boosterItemIdsToBoosterItemsForBoosterPackIds.containsKey(packId)) {
					Map<Integer, BoosterItem> bItemIdToBItem = new HashMap<Integer, BoosterItem>();
					boosterItemIdsToBoosterItemsForBoosterPackIds.put(packId, bItemIdToBItem);
				}
				//each itemId is unique (autoincrementing in the table)
				Map<Integer, BoosterItem> itemIdToItem =
						boosterItemIdsToBoosterItemsForBoosterPackIds.get(packId);
				itemIdToItem.put(bi.getId(), bi);
			}
		} catch (Exception e) {
			log.error("error creating a map of booster item ids to booster items.", e);
		}
	}
	

	private void setStaticBoosterItemIdsToBoosterItems() {
		log.debug("setting  map of boosterItemIds to booster items");
		
		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<BoosterItem> boosterItemList = getBoosterItemEntityManager().get().find(cqlquery);
		
		//fill up the map
		boosterItemIdsToBoosterItems = new HashMap<Integer, BoosterItem>();
		for(BoosterItem m : boosterItemList) {
			Integer intId = m.getId();
			boosterItemIdsToBoosterItems.put(intId, m);
		}
	}



	public  void reload() {
		setStaticBoosterItemIdsToBoosterItems();
		setStaticBoosterItemIdsToBoosterItemsForBoosterPackIds();
	}
	

	
	
	public BoosterItemEntityManager getBoosterItemEntityManager() {
		return boosterItemEntityManager;
	}

	public void setBoosterItemEntityManager(
			BoosterItemEntityManager boosterItemEntityManager) {
		this.boosterItemEntityManager = boosterItemEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
