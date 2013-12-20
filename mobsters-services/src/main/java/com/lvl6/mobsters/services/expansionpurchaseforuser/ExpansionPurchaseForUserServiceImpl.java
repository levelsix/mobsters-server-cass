package com.lvl6.mobsters.services.expansionpurchaseforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ExpansionPurchaseForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.ExpansionCostRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.ExpansionPurchaseForUser;
import com.lvl6.mobsters.po.staticdata.ExpansionCost;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component
public class ExpansionPurchaseForUserServiceImpl implements ExpansionPurchaseForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_EXPANSION_PURCHASE_FOR_USER;
	  
	@Autowired
	protected ExpansionCostRetrieveUtils expansionCostRetrieveUtils;
	
	@Autowired
	protected ExpansionPurchaseForUserEntityManager expansionPurchaseForUserEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	

	//CONTROLLER LOGIC STUFF****************************************************************
	@Override
	public ExpansionCost getExpansionCost(int nthExpansion) {
		ExpansionCost ec = getExpansionCostRetrieveUtils().getCityExpansionCostById(nthExpansion);
		
		return ec;
	}
	
	@Override
	public int calculateExpansionCostCash(int numOfExpansions) {
		ExpansionCost ec = getExpansionCostRetrieveUtils()
				.getCityExpansionCostById(numOfExpansions);
		
		return ec.getExpansionCostCash();
	}
	
	@Override
	public ExpansionPurchaseForUser selectSpecificExpansion(int xPosition, int yPosition,
			List<ExpansionPurchaseForUser> epfuList) {
		
		//if there aren't any expansion purchases return null;
		if (null == epfuList || epfuList.isEmpty()) {
			return null;
		}
		
		//go through each of the user's expansion purchases get the one with the
		//corresponding x and y coordinates
		for (ExpansionPurchaseForUser epfu : epfuList) {
			int x = epfu.getxPosition();
			int y = epfu.getyPosition();
			
			if (x == xPosition && y == yPosition) {
				return epfu;
			}
		}
		
		return null;
	}
	
	//RETRIEVING STUFF****************************************************************
	@Override
	public List<ExpansionPurchaseForUser> getExpansionPurchasesForUser(UUID userId) {
		log.debug("retrieving expansion purchase for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.EXPANSION_PURCHASE_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement); 
		List<ExpansionPurchaseForUser> epfuList = getExpansionPurchaseForUserEntityManager()
				.get().find(cqlquery);

		return epfuList;
	}
	
	@Override
	public ExpansionPurchaseForUser getSpecificExpansionPurchaseForUser(UUID userId,
			int xPosition, int yPosition) {
		log.debug("retrieving specific expansion purchase for userId=" + userId + 
				"\t xPosition=" + xPosition + "\t yPosition=" + yPosition);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.EXPANSION_PURCHASE_FOR_USER__USER_ID, userId);
		equalityConditions.put(MobstersDbTables.EXPANSION_PURCHASE_FOR_USER__X_POSITION, xPosition);
		equalityConditions.put(MobstersDbTables.EXPANSION_PURCHASE_FOR_USER__Y_POSITION, yPosition);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement); 
		List<ExpansionPurchaseForUser> epfuList = getExpansionPurchaseForUserEntityManager()
				.get().find(cqlquery);
		if (null == epfuList || epfuList.isEmpty()) {
			return null;
		}
		
		if (epfuList.size() > 1) {
			log.error("unexpected error: more than one ExpansionPurchase exists. expansions="
					+ epfuList + ". not returning anything");
			return null;
		}
		
		return epfuList.get(0);
	}
	
	//INSERTING STUFF****************************************************************
	@Override
	public ExpansionPurchaseForUser createNewUserExpansionPurchaseForUser(UUID userId, int expansionPurchaseId, Date now) {
		ExpansionPurchaseForUser qfu = new ExpansionPurchaseForUser();
//		qfu.setUserId(userId);
//		qfu.setExpansionPurchaseId(expansionPurchaseId);
//		qfu.setTimeAccepted(now);
//		//the other columns are default values for new ExpansionPurchaseForUser
//		
//		log.info("saving new expansionPurchase for user. qfu=" + qfu);
//		saveExpansionPurchaseForUser(qfu);
		
		return qfu;
	}
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveExpansionPurchaseForUser(ExpansionPurchaseForUser qfu) {
		getExpansionPurchaseForUserEntityManager().get().put(qfu);
	}
	
	@Override
	public void saveExpansionPurchasesForUser(Collection<ExpansionPurchaseForUser> qfuList) {
		getExpansionPurchaseForUserEntityManager().get().put(qfuList);
	}
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public ExpansionCostRetrieveUtils getExpansionCostRetrieveUtils() {
		return expansionCostRetrieveUtils;
	}
	@Override
	public void setExpansionCostRetrieveUtils(ExpansionCostRetrieveUtils expansionPurchaseRetrieveUtils) {
		this.expansionCostRetrieveUtils = expansionPurchaseRetrieveUtils;
	}

	@Override
	public ExpansionPurchaseForUserEntityManager getExpansionPurchaseForUserEntityManager() {
		return expansionPurchaseForUserEntityManager;
	}
	@Override
	public void setExpansionPurchaseForUserEntityManager(
			ExpansionPurchaseForUserEntityManager expansionPurchaseForUserEntityManager) {
		this.expansionPurchaseForUserEntityManager = expansionPurchaseForUserEntityManager;
	}
	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
