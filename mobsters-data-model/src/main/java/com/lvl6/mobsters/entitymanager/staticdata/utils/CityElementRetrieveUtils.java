package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.CityElementEntityManager;
import com.lvl6.mobsters.po.staticdata.CityElement;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class CityElementRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, List<CityElement>> cityIdToCityElements;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_CITY_ELEMENT;

	@Autowired
	protected CityElementEntityManager cityElementEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;



	public Map<Integer, List<CityElement>> getCityIdToCityElements() {
		log.debug("retrieving all city id to neutral city elements data");
		if (cityIdToCityElements == null) {
			setStaticCityIdsToCityElements();
		}
		return cityIdToCityElements;
	}

	public CityElement getCityElement(int cityId, int assetId) {
		log.debug("retrieving neutral city element with assetId " + assetId + " in cityId " + cityId);
		if (cityIdToCityElements == null) {
			setStaticCityIdsToCityElements();
		}
		List<CityElement> ncesForCity = cityIdToCityElements.get(cityId);
		if (ncesForCity != null) {
			for (CityElement nce : ncesForCity) {
				if (nce.getAssetId() == assetId) {
					return nce;
				}
			}
		}
		return null;
	}

	public List<CityElement> getCityElementsForCity(int cityId) {
		log.debug("retrieving neutral city elements for city " + cityId);
		if (cityIdToCityElements == null) {
			setStaticCityIdsToCityElements();
		}
		return cityIdToCityElements.get(cityId);
	}

	private void setStaticCityIdsToCityElements() {
		log.debug("setting  map of cityElementIds to booster items");

		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<CityElement> cityElementList = getCityElementEntityManager().get().find(cqlquery);

		//fill up the map
		cityIdToCityElements = new HashMap<Integer, List<CityElement>>();
		for(CityElement ce : cityElementList) {
			Integer cityId = ce.getCityId();

			//check base case where no city elements for city id is recorded
			if (!cityIdToCityElements.containsKey(cityId)) {
				List<CityElement> elements = new ArrayList<CityElement>();
				cityIdToCityElements.put(cityId, elements);
			}
			List<CityElement> elements = cityIdToCityElements.get(cityId);
			elements.add(ce);
		}
	}



	public  void reload() {
		setStaticCityIdsToCityElements();
	}




	public CityElementEntityManager getCityElementEntityManager() {
		return cityElementEntityManager;
	}

	public void setCityElementEntityManager(
			CityElementEntityManager cityElementEntityManager) {
		this.cityElementEntityManager = cityElementEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

}
