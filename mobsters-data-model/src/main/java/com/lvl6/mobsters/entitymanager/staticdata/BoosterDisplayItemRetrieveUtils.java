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

	private  Map<Integer, BoosterDisplayItem> boosterDisplayItemIdsToBoosterDisplayItems;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_BOOSTER_DISPLAY_ITEM;

	@Autowired
	protected BoosterDisplayItemEntityManager boosterDisplayItemEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	

	public Map<Integer, BoosterDisplayItem> getBoosterDisplayItemIdsToBoosterDisplayItems() {
		log.debug("retrieving all booster display items data map");
		if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
		}
		return boosterDisplayItemIdsToBoosterDisplayItems;
	}

	public BoosterDisplayItem getBoosterDisplayItemForBoosterDisplayItemId(int boosterDisplayItemId) {
		log.debug("retrieve booster item data for booster item " + boosterDisplayItemId);
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
		for(BoosterDisplayItem m : boosterDisplayItemList) {
			Integer intId = m.getId();
			boosterDisplayItemIdsToBoosterDisplayItems.put(intId, m);
		}
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
