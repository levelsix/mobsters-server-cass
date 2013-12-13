package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.BoosterPack;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class BoosterPackRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, BoosterPack> boosterPackIdsToBoosterPacks;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_BOOSTER_PACK;

	@Autowired
	protected BoosterPackEntityManager boosterPackEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	

	public Map<Integer, BoosterPack> getBoosterPackIdsToBoosterPacks() {
		log.debug("retrieving all booster packs data map");
		if (boosterPackIdsToBoosterPacks == null) {
			setStaticBoosterPackIdsToBoosterPacks();
		}
		return boosterPackIdsToBoosterPacks;
	}

	public BoosterPack getBoosterPackForBoosterPackId(int boosterPackId) {
		log.debug("retrieve booster pack data for booster pack " + boosterPackId);
		if (boosterPackIdsToBoosterPacks == null) {
			setStaticBoosterPackIdsToBoosterPacks();      
		}
		return boosterPackIdsToBoosterPacks.get(boosterPackId);
	}
	

	private void setStaticBoosterPackIdsToBoosterPacks() {
		log.debug("setting  map of boosterPackIds to booster packs");
		
		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<BoosterPack> boosterPackList = getBoosterPackEntityManager().get().find(cqlquery);
		
		//fill up the map
		boosterPackIdsToBoosterPacks = new HashMap<Integer, BoosterPack>();
		for(BoosterPack m : boosterPackList) {
			Integer intId = m.getId();
			boosterPackIdsToBoosterPacks.put(intId, m);
		}
	}



	public  void reload() {
		setStaticBoosterPackIdsToBoosterPacks();
	}
	

	
	
	public BoosterPackEntityManager getBoosterPackEntityManager() {
		return boosterPackEntityManager;
	}

	public void setBoosterPackEntityManager(
			BoosterPackEntityManager boosterPackEntityManager) {
		this.boosterPackEntityManager = boosterPackEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
