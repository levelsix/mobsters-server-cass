package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.ProfanityEntityManager;
import com.lvl6.mobsters.po.staticdata.Profanity;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class ProfanityRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private Map<Integer, Profanity> profanityIdsToProfanity;
	private Map<Integer, String> profanityIdsToTerms;

	private final String TABLE_NAME = MobstersDbTables.TABLE_PROFANITY;
	
	@Autowired
	private ProfanityEntityManager profanityEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public Set<String> getAllProfanityTerms() {
		log.debug("setting map of ids to profanity");
		if (null == profanityIdsToTerms) {
			setStaticProfanityTerms();
		}
		Set<String> allTerms = new HashSet<String>(profanityIdsToTerms.values()); 
		return allTerms;
	}

	private void setStaticProfanityTerms() {
		log.debug("setting static set of profanity");
		
		if (null == profanityIdsToProfanity) {
			setStaticProfanity();
		} else if (null == profanityIdsToTerms) {
			profanityIdsToTerms = new HashMap<Integer, String>();
			for (Profanity p : profanityIdsToProfanity.values()) {
				int id = p.getId();
				String term = p.getProfanityTerm();
				profanityIdsToTerms.put(id, term);
			}
		}
		
	}

	public void setStaticProfanity() {

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
		List<Profanity> profanityList = getProfanityEntityManager().get().find(cqlquery);

		//fill up the map
		profanityIdsToProfanity = new HashMap<Integer, Profanity>();
		profanityIdsToTerms = new HashMap<Integer, String>();
		for(Profanity p : profanityList) {
			int id = p.getId();
			profanityIdsToProfanity.put(id, p);
			
			String term = p.getProfanityTerm();
			profanityIdsToTerms.put(id, term);
		}
	}



	public  void reload() {
		setStaticProfanityTerms();
	}
	


	public ProfanityEntityManager getProfanityEntityManager() {
		return profanityEntityManager;
	}

	public void setProfanityEntityManager(
			ProfanityEntityManager profanityEntityManager) {
		this.profanityEntityManager = profanityEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
