package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.CityEntityManager;
import com.lvl6.mobsters.po.staticdata.City;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class CityRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, City> cityIdToCity;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_CITY;

	@Autowired
	protected CityEntityManager cityEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public City getCityForCityId(int cityId) {
		log.debug("retrieve booster pack data for booster pack " + cityId);
		if (cityIdToCity == null) {
			setStaticCityIdsToCities();      
		}
		return cityIdToCity.get(cityId);
	}

	public Map<Integer, City> getCityIdsToCities() {
		log.debug("retrieving all booster packs data map");
		if (cityIdToCity == null) {
			setStaticCityIdsToCities();
		}
		return cityIdToCity;
	}
	

	private void setStaticCityIdsToCities() {
		log.debug("setting map of cityIds to cities");
		
		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		boolean allowFiltering = false; //don't let cassandra query with non row keys
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement, allowFiltering);
		List<City> cityList = getCityEntityManager().get().find(cqlquery);
		
		//fill up the map
		cityIdToCity = new HashMap<Integer, City>();
		for(City c : cityList) {
			Integer intId = c.getId();
			cityIdToCity.put(intId, c);
		}
	}



	public  void reload() {
		setStaticCityIdsToCities();
	}
	

	
	
	public CityEntityManager getCityEntityManager() {
		return cityEntityManager;
	}

	public void setCityEntityManager(
			CityEntityManager cityEntityManager) {
		this.cityEntityManager = cityEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
