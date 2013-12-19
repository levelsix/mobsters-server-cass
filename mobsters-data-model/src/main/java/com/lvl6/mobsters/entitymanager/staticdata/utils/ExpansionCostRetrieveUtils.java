package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.ExpansionCostEntityManager;
import com.lvl6.mobsters.po.staticdata.ExpansionCost;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class ExpansionCostRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, ExpansionCost> expansionNumToExpansionCost;
	
	private  final String TABLE_NAME = MobstersDbTables.TABLE_EXPANSION_COST;

	@Autowired
	protected ExpansionCostEntityManager expansionCostEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	

	public Map<Integer, ExpansionCost> getAllExpansionNumsToCosts() {
		if (null == expansionNumToExpansionCost) {
			setStaticExpansionNumToCityExpansionCost();
		}

		return expansionNumToExpansionCost;
	}

	
	public ExpansionCost getCityExpansionCostById(int id) {
		if (null == expansionNumToExpansionCost) {
			setStaticExpansionNumToCityExpansionCost();
		}

		return expansionNumToExpansionCost.get(id);
	}
	
	
	private  void setStaticExpansionNumToCityExpansionCost() {
		log.debug("setting map of expansion cost ids to expansion costs");

		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<ExpansionCost> list = getExpansionCostEntityManager().get().find(cqlquery);
		expansionNumToExpansionCost = new HashMap<Integer, ExpansionCost>();
		for(ExpansionCost ec : list) {
			Integer id= ec.getId();
			expansionNumToExpansionCost.put(id, ec);
		}		
	}
	

	public  void reload() {
		setStaticExpansionNumToCityExpansionCost();
	}
	
	

	public ExpansionCostEntityManager getExpansionCostEntityManager() {
		return expansionCostEntityManager;
	}

	public void setExpansionCostEntityManager(
			ExpansionCostEntityManager expansionCostEntityManager) {
		this.expansionCostEntityManager = expansionCostEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
